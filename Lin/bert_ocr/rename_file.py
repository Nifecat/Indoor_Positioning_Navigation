import os


def rename(path):
    """重命名文件， train1, train2..."""
    path = path
    files = os.listdir(path)
    for i, file in enumerate(files):
        filetype = os.path.splitext(file)[1]
        NewName = os.path.join(path, 'train' + str(i) + '.jpg')
        OldName = os.path.join(path, file)
        os.rename(OldName, NewName)

for i in range(0, 18):
    path = 'data/train'
    rename(path+'/{}'.format(i))
# path = "C:/Users/JeremyLin/Desktop/PycharmProjects/bert_ocr/data/train/clothes"
# files = os.listdir(path)
# print(files)
# # filetype = os.path.splitext(files[10])[1]
# # print(filetype)
# for i, file in enumerate(files):
#     filetype = os.path.splitext(file)[1]
#     NewName = os.path.join(path, 'train' + str(i) + '.jpg')
#     OldName = os.path.join(path, file)
#     os.rename(OldName, NewName)