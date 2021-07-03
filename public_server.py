import socket

server_for_client = socket.socket()
server_for_client.bind(('0.0.0.0', 6409))

server_for_gpu = socket.socket()
server_for_gpu.bind(('0.0.0.0', 6411))


server_for_client.listen(1)
server_for_gpu.listen(1)
print('listening...')

try:
    while True:
        remote_gpu, remote_gpu_addr = server_for_gpu.accept()
        print('connected with gpu')

        client, addr, = server_for_client.accept()
        print(f'{addr} connected')

        data = b''
        while True:

            data_recv = client.recv(4096)
            if not data_recv:
                break

            data += data_recv


        remote_gpu.sendall(data)
        remote_gpu.shutdown(socket.SHUT_WR)

        data = b''
        while True:

            data_recv = remote_gpu.recv(4096)
            if not data_recv:
                break

            data += data_recv

        print(data)

        client.sendall(data)

except InterruptedError as e:
    server_for_client.close()
    server_for_gpu.close()
except Exception as e:
    server_for_client.close()
    server_for_gpu.close()
