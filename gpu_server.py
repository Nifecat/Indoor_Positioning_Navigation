import base64
import json
import socket
from main_model import MainModel
# client = socket()
# client.bind(('0.0.0.0', 6409))
#
#
# client.listen(1)
# print('listening...')

print('initing model...')
model = MainModel()
print('model loaded')



try:
    while True:
        print('connecting to server')
        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server.connect(('120.46.155.57', 6411))
        print('server connected')


        data = b''
        while True:

            data_recv = server.recv(4096)
            if not data_recv:
                break

            data += data_recv

        # print(data)


        data = json.loads(data)

        # print(data)
        print(len(data['image']))
        print(data['wifi'])

        with open('tmp.jpg', 'wb') as f:
            f.write(base64.b64decode(data['image']))
            f.flush()
            f.close()
            print('image saved')


        # wifi to txt
        with open('wifitmp.tmp', 'w') as f:
            f.write("-361.0\n")
            for ws in data['wifi']:
                f.write(f'{ws["name"]} {ws["rssi"]}\n')
            f.flush()
            f.close()
            print('wifi saved')

        ret = model.predict('tmp.jpg', 'wifitmp.tmp')

        server.send(str(ret).encode())

        server.close()

except Exception as e:
    server.close()