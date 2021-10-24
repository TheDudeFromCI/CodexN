import torch


class Connection:
    def __init__(self, node_index, node_type, plug_index, plug_type, h=0):
        self.node_index = node_index
        self.node_type = node_type
        self.plug_index = plug_index
        self.plug_type = plug_type
        self.pos = 0
        self.heuristic = h


class Graph:
    def __init__(self):
        self.connections = []
        self.possible_next = []

    def add_connection(self, connection):
        self.connections.append(connection)
        connection.pos = len(self.connections) - 1

    def add_next(self, connection):
        self.possible_next.append(connection)
