import easyocr
import os
import time
import warnings
warnings.filterwarnings("ignore")
from datetime import datetime
# from pytorch_pretrained import BertTokenizer, BertModel

# cpu计算
reader = easyocr.Reader(['ch_sim', 'en'])
starttime = datetime.now()
print('start')
result = []

# print(reader.readtext(image='data/train/1/train0.jpg', decoder='greedy', batch_size=20, detail=0))
# decoder 为引擎，detail 为是否显示位置信息 batch_size 设置越大，占用内存越高，识别速度越快
for i in range(0, len(os.listdir('data/train/1'))):
    label = 1
    text = reader.readtext(image='data/train/1/train{}.jpg'.format(i), decoder='greedy', batch_size=20, detail=0)
    print(text, label)

    # try:
    #     text = reader.readtext(image='data/train/1/train{}.jpg'.format(i), decoder='greedy', batch_size=20, detail=0)
    #     print(text + ' ' + int(label))
    # except Exception:
    #     print('error', i)
    #     continue

endtime = datetime.now()
# print('cpu need time', (endtime - starttime).seconds, 's')
# for i in range(len(result)):
#     print(result[i])
#     print('\n')

#gpu计算
# reader = easyocr.Reader(['ch_sim', 'en'])
# starttime = datetime.now()
# print('start')
# result = reader.readtext(image='test.jpg', decoder='greedy', batch_size=20,detail=0)
# endtime=datetime.now()
# print('gpu need time', (endtime-starttime).seconds,'s')
# print(result)

# result = ['我ai你,再见', '软件项目管理']

# Bert
# tokenizer = BertTokenizer.from_pretrained('bert-base-chinese')
# bert_model = BertModel.from_pretrained('bert-base-chinese')
#
# for i in range(len(result)):
#     inputs = tokenizer(result[i], return_tensors='pt')
#     # inputs = tokenizer(result[i])
#     print(inputs)
#
#     tokenizer.decode(inputs['input_ids'].data.cpu().numpy().reshape(-1))
#
#     outputs = bert_model(**inputs)
#     sequence_outputs, pooled_outputs = outputs.last_hidden_state, outputs.pooler_output
#
#     print('sequence_outputs shape:', sequence_outputs.shape)
#     print('pooled_outputs shape:', pooled_outputs.shape)
#
# for i in range(len(result)):
#     token = tokenizer.tokenize(result[i])
#     print("token: ", token)
#     print("token len: ", len(token))
#
#     token_to_id = tokenizer.convert_tokens_to_ids(token)
#     print("token_to_id: ", token_to_id)