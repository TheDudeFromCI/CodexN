import socket
import struct

class SocketClosedError(Exception):
    pass


class BufferedStream:
    def __init__(self, conn):
        self.conn = conn
        self.in_buffer = []
        self.out_buffer = []

    def get(self, bytes=1024):
        data = self.conn.recv(bytes)
        if not data: raise SocketClosedError
        self.in_buffer += data
    
    def ensure_capacity(self, amount):
        while len(self.in_buffer) < amount:
            self.get()
    
    def get_int(self):
        self.ensure_capacity(4)

        val = int.from_bytes(self.in_buffer[:4], 'big', signed=True)
        self.in_buffer = self.in_buffer[4:]
        return val

    def write_int(self, val):
        self.out_buffer += val.to_bytes(4, 'big')

    def get_float(self):
        val = self.get_int()
        s = struct.pack('>l', val)
        return struct.unpack('>f', s)[0]
    
    def write_float(self, val):
        s = struct.pack('>f', val)
        self.write_int(struct.unpack('>l', s)[0])

    def flush(self):
        self.conn.sendall(bytearray(self.out_buffer))
        self.out_buffer = []


def handle_packet(handlers, buffered_stream):
    packet_type = buffered_stream.get_int()

    if packet_type in handlers:
        handlers[packet_type](buffered_stream)
    else:
        print('Unknown packet type: {}'.format(packet_type))
        raise SocketClosedError


def start_server(handlers, port=8246):
    print('Starting server on port {}...'.format(port))

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

        host = '127.0.0.1'
        s.bind((host, port))
        s.listen(1)

        try:
            while True:
                print('Waiting for client...')

                conn, addr = s.accept()
                print('Client connected at {}.'.format(addr))

                with conn:
                    buffered_stream = BufferedStream(conn)
                    while True:
                        try:
                            handle_packet(handlers, buffered_stream)
                        except SocketClosedError:
                            break

                print('Client {} disconnected.'.format(addr))

        except KeyboardInterrupt:
            print('Server shutdown requested from terminal.')
        
    s.close()