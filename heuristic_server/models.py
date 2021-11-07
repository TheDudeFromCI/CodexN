import math
import torch
import torch.nn as nn
import torch.nn.functional as F
from torch.nn.utils.rnn import pad_sequence
from torch.optim import Adam


class PositionalEncoding(nn.Module):
    def __init__(self, d_model, max_len=100):
        super(PositionalEncoding, self).__init__()

        pe = torch.zeros(max_len, d_model)
        position = torch.arange(0, max_len, dtype=torch.float).unsqueeze(1)
        div_term = torch.exp(torch.arange(0, d_model, 2).float() * (-math.log(10000.0) / d_model))
        pe[:, 0::2] = torch.sin(position * div_term)
        pe[:, 1::2] = torch.cos(position * div_term)
        pe = pe.unsqueeze(0).transpose(0, 1)
        self.register_buffer('pe', pe)

    def forward(self, x):
        return x + self.pe[x.size(0), :]


class GraphEncoder(nn.Module):
    def __init__(self, max_nodes, max_plugs, max_connections, num_node_types, num_data_types, connection_dim=64):
        super(GraphEncoder, self).__init__()

        self.max_nodes = max_nodes
        self.max_plugs = max_plugs
        self.max_connections = max_connections
        self.num_node_types = num_node_types
        self.num_data_types = num_data_types

        self.node_index_embed = nn.Embedding(max_nodes, connection_dim)
        self.node_type_embed = nn.Embedding(num_node_types, connection_dim)
        self.plug_index_embed = nn.Embedding(max_plugs, connection_dim)
        self.plug_type_embed = nn.Embedding(num_data_types, connection_dim)
        self.pos_encoding = PositionalEncoding(connection_dim, max_len=max_connections)

    def build_connection_matrix(self, connections, device):
        def tensor(array):
            return torch.tensor(array, dtype=torch.long, device=device)

        x = self.node_index_embed(tensor([c.node_index for c in connections]))
        x += self.node_type_embed(tensor([c.node_type for c in connections]))
        x += self.plug_index_embed(tensor([c.plug_index for c in connections]))
        x += self.plug_type_embed(tensor([c.plug_type for c in connections]))
        return self.pos_encoding(x)

    def build_heuristics_matrix(self, connections, device):
        return torch.tensor([c.heuristic for c in connections], dtype=torch.float, device=device)

    def forward(self, graphs):
        device = next(self.parameters()).device

        x1 = []
        x2 = []
        y = []
        x1_mask = []
        x2_mask = []
        x1_max = 0
        x2_max = 0

        with torch.no_grad():
            for graph in graphs:
                x1.append(self.build_connection_matrix(graph.connections, device))
                x2.append(self.build_connection_matrix(graph.possible_next, device))
                y.append(self.build_heuristics_matrix(graph.possible_next, device))
                
                x1_max = max(x1_max, len(graph.connections))
                x2_max = max(x2_max, len(graph.possible_next))

            for graph in graphs:
                x1_size = len(graph.connections)
                x1_mask.append([False for _ in range(x1_size)] + [True for _ in range(x1_max - x1_size)])

                x2_size = len(graph.possible_next)
                x2_mask.append([False for _ in range(x2_size)] + [True for _ in range(x2_max - x2_size)])

            x1_mask = torch.tensor(x1_mask, dtype=torch.bool, device=device)
            x2_mask = torch.tensor(x2_mask, dtype=torch.bool, device=device)
            return pad_sequence(x1), pad_sequence(x2), pad_sequence(y), x1_mask, x2_mask

class GraphRNN(nn.Module):
    def __init__(self, embed_size, n_heads, encoder_layers, decoder_layers, dim_fc, dropout=0.2):
        super(GraphRNN, self).__init__()

        self.embed_size = embed_size
        self.n_heads = n_heads
        self.encoder_layers = encoder_layers
        self.decoder_layers = decoder_layers
        self.dim_fc = dim_fc
        self.dropout = dropout

        self.transformer = nn.Transformer(d_model=embed_size, nhead=n_heads, num_encoder_layers=encoder_layers,
                             num_decoder_layers=decoder_layers, dim_feedforward=dim_fc)
        
        self.activation = nn.Sigmoid()

    def forward(self, problem, solution, next_vectors, problem_mask, solution_mask, next_vectors_mask):
        x = self.transformer(solution, problem, src_key_padding_mask=solution_mask, tgt_key_padding_mask=problem_mask)

        x = torch.einsum('qnd,knd->nqk', [x, next_vectors])
        x = torch.mean(x, dim=1)
        return self.activation(x)

class GraphSolver(nn.Module):
    def __init__(self, problem_encoder, solution_encoder, rnn, lr=0.01):
        super(GraphSolver, self).__init__()

        self.problem_encoder = problem_encoder
        self.solution_encoder = solution_encoder
        self.rnn = rnn
        self.optimizer = Adam(self.parameters(), lr=lr)
    
    def forward(self, problems, solutions, train=True):
        if train:
            self.optimizer.zero_grad()

        problems, _, _, problem_mask, _ = self.problem_encoder(problems)
        solutions, next_vectors, target_vectors, solution_mask, next_vectors_mask = self.solution_encoder(solutions)
        real_vectors = self.rnn(problems, solutions, next_vectors, problem_mask, solution_mask, next_vectors_mask)

        if train:
            loss = F.mse_loss(real_vectors, target_vectors)
            loss.backward()
            self.optimizer.step()
            return loss.item()

        else:
            return real_vectors.detach().numpy()
