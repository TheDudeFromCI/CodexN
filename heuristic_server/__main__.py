import torch
import torch.nn as nn
import numpy as np
from numpy.random import default_rng

BATCH_SIZE = 24
SEQ_LENGTH = 64
MAX_NODES = 24
MAX_INPUT_PLUGS = 8
MAX_OUTPUT_PLUGS = 8
NUM_NODE_TYPES = 16
NUM_DATA_TYPES = 16
CONNECTION_SIZE = 4  # Connection = (Node Type, Node Index, Data Type, Data Index)

N_HEADS = 8
EMBED_SIZE = 512
ENCODER_LAYERS = 6
DECODER_LAYERS = 6
DIM_FC = 1024
DROPOUT = 0.2

rng = default_rng()

# Temporary
connections_target = torch.tensor(rng.randint(MAX_INPUT_PLUGS, size=(BATCH_SIZE, SEQ_LENGTH, CONNECTION_SIZE)))
connections_current = torch.tensor(rng.randint(MAX_INPUT_PLUGS, size=(BATCH_SIZE, SEQ_LENGTH, CONNECTION_SIZE)))

node_type_embed = nn.Embedding(MAX_NODES, )

transformer = nn.Transformer(d_model=EMBED_SIZE, nhead=N_HEADS, num_encoder_layers=ENCODER_LAYERS,
                             num_decoder_layers=DECODER_LAYERS, dim_feedforward=DIM_FC, batch_first=True)

src = torch.rand((10, 32, 300))
tgt = torch.rand((20, 32, 300))

out = transformer(src, tgt)
print(out.shape)
