# coding: UTF-8
import torch
from utils import load_dataset, build_iterator
from train_eval import train
from models import bert
from pytorch_pretrained.optimization import BertAdam
import time
from datetime import timedelta
from sklearn import metrics
from torch import nn
import torch.nn.functional as F


def get_time_dif(start_time):
    """获取已使用时间"""
    end_time = time.time()
    time_dif = end_time - start_time
    return timedelta(seconds=int(round(time_dif)))


# def fine_tune(config, model, logo_data_iter):
#     model_path = './THUCNews/save_dict/bert.ckpt'
#     model.load_state_dict(torch.load(model_path))
#     start_time = time.time()
#     # model.train()
#     for name, param in model.named_parameters():
#         if name == 'fc.bias' or name == 'fc.weight':
#             continue
#         else:
#             param.requires_grad = False
#     # model.fc.requires_grad = True
#     # param_optimizer = list(model.fc.parameters())
#     # no_decay = ['bias', 'LayerNorm.bias', 'LayerNorm.weigt']
#     # optimizer_group_parameters = [
#     #     {'params': [p for n, p in param_optimizer if not any(nd in n for nd in no_decay)], 'weight_decay': 0.01},
#     #     {'params': [p for n, p in param_optimizer if any(nd in n for nd in no_decay)], 'weight_decay': 0.0}]
#     model.fc2 = nn.Linear(10, 18)
#     print(model)
#     optimizer = BertAdam([{'params': model.fc.parameters()},
#                           {'params': model.fc2.parameters()}],
#                          lr=config.learning_rate,
#                          warmup=0.05,
#                          t_total=len(logo_data_iter) * 10)
#     total_batch = 0
#     dev_best_loss = float('inf')
#     last_improve = 0
#     flag = False
#     model.train()
#     for epoch in range(config.num_epochs):
#         print('Epoch [{}/{}]'.format(epoch + 1, config.num_epochs))
#         for i, (logo, labels) in enumerate(logo_data_iter):
#             outputs = model(logo)
#             outputs = model.fc2(outputs)
#             model.zero_grad()
#             loss = F.cross_entropy(outputs, labels)
#             loss.backward()
#             optimizer.step()
#             if total_batch % 10 == 0:
#                 # 每多少轮输出在训练集和验证集上的效果
#                 true = labels.data.cpu()
#                 predic = torch.max(outputs.data, 1)[1].cpu()
#                 train_acc = metrics.accuracy_score(true, predic)
#                 # dev_acc, dev_loss = evaluate(config, model, dev_iter)
#                 # if dev_loss < dev_best_loss:
#                 #     dev_best_loss = dev_loss
#                 #     torch.save(model.state_dict(), config.save_path)
#                 #     improve = '*'
#                 #     last_improve = total_batch
#                 # else:
#                 #     improve = ''
#                 time_dif = get_time_dif(start_time)
#                 # msg = 'Iter: {0:>6},  Train Loss: {1:>5.2},  Train Acc: {2:>6.2%},  Val Loss: {3:>5.2},  Val Acc: {4:>6.2%},  Time: {5} {6}'
#                 # print(msg.format(total_batch, loss.item(), train_acc, dev_loss, dev_acc, time_dif, improve))
#                 msg = 'Iter: {0:>6},  Train Loss: {1:>5.2},  Train Acc: {2:>6.2%}, Time: {3}'
#                 print(msg.format(total_batch, loss.item(), train_acc, time_dif))
#                 model.train()
#             total_batch += 1
#             if total_batch - last_improve > 1000:
#                 # 验证集loss超过1000batch没下降，结束训练
#                 print("No optimization for a long time, auto-stopping...")
#                 flag = True
#                 break
#         if flag:
#             break

dataset = 'THUCNews'
config = bert.Config(dataset)
model = bert.Model(config).to(config.device)

logo_data = load_dataset(config=config, path='data/train/class', pad_size=32, fine_tune_flag=True)
# print(logo_data[490]) # 1012
logo_data_iter = build_iterator(logo_data, config, 1)
# for i, (trains, labels) in enumerate(logo_data_iter):
#     if i % 100 == 0:
#         print(trains)
#         print(labels)
# print(logo_data_iter)
train(config, model, logo_data_iter, dev_iter=None, fine_tune_flag=True)

# test(config, model, logo_data_iter)
# fine_tune(config, model, logo_data_iter)


