import easyocr
import time
from datetime import datetime
import os

# cpu计算, False
reader = easyocr.Reader(['ch_sim', 'en'])
starttime = datetime.now()
print('start')
# result = []
# total_len = len(os.listdir('data/train/0'))
# batch_size = 10
# flag = False
# if total_len % 10 == 0:
#     n_epoch = total_len / batch_size
# else:
#     flag = True
#     n_epoch = total_len // batch_size + 1
#
# index = 0
# decoder 为引擎，detail 为是否显示位置信息 batch_size 设置越大，占用内存越高，识别速度越快
for i in range(0, 18): #ERROR: 46 64 90 xxx
    print('Folder number {}'.format(i))
    for j in range(0, len(os.listdir('data/train/{}'.format(i)))+1):
        label = i
        # result.append(reader.readtext(image='./data/train/{0}/train{1}.jpg'.format(i, j), decoder='greedy', batch_size=20, detail=0))
        try:
            result = reader.readtext(image='data/train/{0}/train{1}.jpg'.format(i, j), decoder='greedy', batch_size=20, detail=0)
            fh = open('ocr.txt', 'a', encoding='utf-8')
            cont = ' '.join(result)
            cont = ''.join(cont.split())  # 去掉字符串中间的空格
            fh.write(cont)
            fh.write(' ' + str(label))
            fh.write('\n')
        except Exception:
            print('Image number {} error'.format(j))
            continue

fh.close()

endtime = datetime.now()
print('cpu need time', (endtime - starttime).seconds, 's')


# endtime = datetime.now()
# print('cpu need time', (endtime - starttime).seconds, 's')
# print(result)
# fh = open('ocr.txt', 'w', encoding='utf-8')
# for i in range(len(result)):
#     str = ' '.join(result[i])
#     str = ''.join(str.split()) # 去掉字符串中间的空格
#     fh.write(str + '\n')
#     print(result[i])
#     print('\n')
#
# fh.close()