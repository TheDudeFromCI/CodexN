import argparse

from server import start_server
from graph import Connection, Graph
from models import GraphEncoder, GraphRNN, GraphSolver

parser = argparse.ArgumentParser(
    prog='heuristic_server',
    description='A microservice for generating heuristic estimates within a node graph serch tree.',
    usage='%(prog)s [options]')

parser.add_argument(
    '--port',
    type=int,
    nargs=1,
    default=8246,
    help='the port to to run the server on')

parser.add_argument(
    '--lr',
    type=float,
    nargs=1,
    default=0.01,
    help='the learning rate')

parser.add_argument(
    '--n_heads',
    type=int,
    nargs=1,
    default=8,
    help='the number of attention heads')

parser.add_argument(
    '--embed_size',
    type=int,
    nargs=1,
    default=512,
    help='the connection embedding size')

parser.add_argument(
    '--max_nodes',
    type=int,
    nargs=1,
    default=48,
    help='the maximum number of nodes in a graph')

parser.add_argument(
    '--max_plugs',
    type=int,
    nargs=1,
    default=8,
    help='the maximum number of plugs in a node')

parser.add_argument(
    '--max_connections',
    type=int,
    nargs=1,
    default=72,
    help='the maximum number of connections in a graph')

parser.add_argument(
    '--encoder_layers',
    type=int,
    nargs=1,
    default=8,
    help='the number of encoder layers')

parser.add_argument(
    '--decoder_layers',
    type=int,
    nargs=1,
    default=8,
    help='the number of decoder layers')

parser.add_argument(
    '--dim_fc',
    type=int,
    nargs=1,
    default=1024,
    help='the dimensionality of the fully connected layers')

parser.add_argument(
    '--dropout',
    type=float,
    nargs=1,
    default=0.2,
    help='the dropout rate')

args = parser.parse_args()

# TODO Load node embeds
# TODO Load weight params

NUM_NODE_TYPES = 16
NUM_DATA_TYPES = 16

max_seq_length = args.max_connections * 2
p_encoder = GraphEncoder(args.max_nodes, args.max_plugs, max_seq_length, NUM_NODE_TYPES, NUM_DATA_TYPES, connection_dim=args.embed_size)
s_encoder = GraphEncoder(args.max_nodes, args.max_plugs, max_seq_length, NUM_NODE_TYPES, NUM_DATA_TYPES, connection_dim=args.embed_size)
rnn = GraphRNN(args.embed_size, args.n_heads, args.encoder_layers, args.decoder_layers, args.dim_fc, dropout=args.dropout)
solver = GraphSolver(p_encoder, s_encoder, rnn, lr=args.lr)

def get_graph(buffered_stream, has_next=False, heuristics=False):
    n_connections = buffered_stream.get_int()
    n_next = buffered_stream.get_int() if has_next else 0

    graph = Graph()

    for _ in range(n_connections):
        node_index = buffered_stream.get_int()
        node_type = buffered_stream.get_int()
        plug_index = buffered_stream.get_int()
        plug_type = buffered_stream.get_int()
        connection = Connection(node_index, node_type, plug_index, plug_type)
        graph.add_connection(connection)

    for _ in range(n_next):
        node_index = buffered_stream.get_int()
        node_type = buffered_stream.get_int()
        plug_index = buffered_stream.get_int()
        plug_type = buffered_stream.get_int()
        connection = Connection(node_index, node_type, plug_index, plug_type)
        if heuristics: connection.heuristic = buffered_stream.get_float()
        graph.add_next(connection)

    return graph


def estimate_heuristics(buffered_stream):
    n_graphs = buffered_stream.get_int()
    solutions_batch = []
    problems_batch = []

    for _ in range(n_graphs):
        solutions_batch.append(get_graph(buffered_stream, has_next=True, heuristics=False))
        problems_batch.append(get_graph(buffered_stream, has_next=False, heuristics=False))
    
    print('Estimating heuristics for {} batches.'.format(n_graphs))
    estimates = solver(problems_batch, solutions_batch, train=False)

    buffered_stream.write_int(n_graphs)
    for i in range(n_graphs):
        solution = solutions_batch[i]
        n_next = len(solution.possible_next)

        buffered_stream.write_int(n_next)
        for j in range(n_next):
            buffered_stream.write_float(estimates[i, j])

    buffered_stream.flush()


def train_heuristics(buffered_stream):
    ...


handlers = {
    0: estimate_heuristics,
    1: train_heuristics,
}

start_server(handlers, port=args.port)
