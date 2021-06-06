import time
import torch
import numpy as np
from models import bert
from train_eval import test
from utils import load_dataset, build_iterator, get_time_dif
import warnings
warnings.filterwarnings("ignore")

if __name__ == '__main__':
    dataset = 'THUCNews'

    config = bert.Config(dataset)
    np.random.seed(666)
    torch.manual_seed(666)
    torch.cuda.manual_seed_all(666)
    torch.backends.cudnn.deterministic = True  # 保证每次结果一样

    start_time = time.time()
    print("Loading data...")
    test_data = load_dataset(config, config.test_path, config.pad_size)
    # train_iter = build_iterator(train_data, config)
    # dev_iter = build_iterator(dev_data, config)
    test_iter = build_iterator(test_data, config)
    time_diff = get_time_dif(start_time)
    print("Time usage: ", time_diff)

    # test
    model = bert.Model(config).to(config.device)
    test(config, model, test_iter)


