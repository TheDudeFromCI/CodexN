from graph import Connection, Graph
from models import GraphEncoder, GraphRNN, GraphSolver

BATCH_SIZE = 24
SEQ_LENGTH = 64
MAX_NODES = 24
MAX_PLUGS = 8
NUM_NODE_TYPES = 16
NUM_DATA_TYPES = 16

N_HEADS = 8
EMBED_SIZE = 64
ENCODER_LAYERS = 6
DECODER_LAYERS = 6
DIM_FC = 1024
DROPOUT = 0.2


graph = Graph()
graph.add_connection(Connection(0, 0, 0, 0))
graph.add_connection(Connection(2, 2, 0, 0))
graph.add_connection(Connection(2, 2, 0, 0))
graph.add_connection(Connection(1, 1, 1, 0))
graph.add_connection(Connection(2, 2, 1, 0))
graph.add_connection(Connection(3, 3, 0, 0))
graph.add_connection(Connection(3, 3, 0, 0))
graph.add_connection(Connection(1, 1, 1, 0))
graph.add_connection(Connection(3, 3, 1, 0))
graph.add_connection(Connection(1, 1, 2, 0))
graph.add_next(Connection(1, 1, 1, 0))
graph.add_next(Connection(2, 2, 2, 0))
graph.add_next(Connection(3, 3, 3, 0))
# 0 = void Output(Float)
# 1 = Float, Float, Float Input()
# 2 = Float Multiply(Float, Float)
# 3 = Float Subtract(Float, Float)
#2: 0 --> 0: 0
#1: 1 --> 2: 0
#3: 0 --> 2: 1
#1: 1 --> 3: 0
#1: 2 --> 3: 1

graph2 = Graph()
graph2.add_connection(Connection(1, 2, 3, 4))
graph2.add_connection(Connection(1, 2, 3, 4))
graph2.add_next(Connection(4, 3, 2, 1))

graph3 = Graph()
graph3.add_connection(Connection(1, 2, 3, 4))
graph3.add_next(Connection(0, 0, 0, 0))
graph3.add_next(Connection(0, 0, 0, 1))

p_encoder = GraphEncoder(MAX_NODES, MAX_PLUGS, SEQ_LENGTH, NUM_NODE_TYPES, NUM_DATA_TYPES,  connection_dim=EMBED_SIZE)
s_encoder = GraphEncoder(MAX_NODES, MAX_PLUGS, SEQ_LENGTH, NUM_NODE_TYPES, NUM_DATA_TYPES,  connection_dim=EMBED_SIZE)
rnn = GraphRNN(EMBED_SIZE, N_HEADS, ENCODER_LAYERS, DECODER_LAYERS, DIM_FC, dropout=DROPOUT)

solver = GraphSolver(p_encoder, s_encoder, rnn, lr=0.01)

print(solver([graph], [graph2], train=True))