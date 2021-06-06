import torch
import torch.nn as nn
# from transformers import BertModel, BertTokenizer
from pytorch_pretrained import BertModel, BertTokenizer


class Config(object):
    """配置参数"""
    def __init__(self, dataset):
        self.model_name = 'bert'
        self.train_path = dataset + '/data/train.txt'
        self.dev_path = dataset + '/data/dev.txt'
        self.test_path = dataset + '/data/test.txt'
        self.class_list = [x.strip() for x in open(dataset + '/data/class.txt').readlines()]  # 类别列表
        self.save_path = dataset + '/save_dict/' + self.model_name + '.ckpt'  # 保存模型训练结果
        self.device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

        self.early_stop_round = 1000   # 超过1000个batch效果没有提升，则提前结束训练
        self.num_classes = len(self.class_list)  # 类别数
        self.num_epochs = 3
        self.batch_size = 128
        self.pad_size = 32       # 每句长度32，短填长切，padding填充
        self.learning_rate = 5e-5
        # self.bert_path = 'bert-base-chinese'   # bert-base-chinese模型，同时支持中文和英文的encoding
        self.bert_path = './bert_pretrain'
        self.tokenizer = BertTokenizer.from_pretrained(self.bert_path)  # input_ids, token_type_ids, attention_mask
        self.hidden_size = 768  # dddd


class Model(nn.Module):

    def __init__(self, config):
        super(Model, self).__init__()
        self.bert = BertModel.from_pretrained(config.bert_path)
        for param in self.bert.parameters():
            param.requires_grad = True
        self.fc = nn.Linear(config.hidden_size, config.num_classes)  # 全连接层
        # self.fc2 = nn.Linear(config.num_classes, 17)

    def forward(self, x):
        context = x[0]  # 输入的句子
        mask = x[2]  # 对padding部分进行mask， 和句子一个size， padding部分用0表示， 如：[1, 1, 1, 1, 0, 0]
        _, pooled = self.bert(context, attention_mask=mask, output_all_encoded_layers=False)
        out = self.fc(pooled)  # [1, 768] -> [768, num_class]
        return out
