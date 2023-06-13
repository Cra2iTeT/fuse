def Select_maxmin(token):
    """

    :param token: 需要分析找出最大最小值的文件的路径
    :return: 双返回值，返回此表中的最小最大的开始时间
    """
    f = open(token, 'r', encoding='UTF-8')
    data = f.readlines()
    f.close()
    data.pop(0)
    data1 = data[0].strip().split(',')
    MaxTime = data1[1]
    MinTime = data1[1]
    # print(MaxTime)
    for line in data:
        line = line.strip().split(',')
        # print(line[1])
        if MaxTime < line[1]:
            MaxTime = line[1]
        if MinTime > line[1]:
            MinTime = line[1]
    print(MaxTime)
    print(MinTime)
    return MaxTime, MinTime


if __name__ == '__main__':
    max, min = Select_maxmin('D:/idea1/fuse/dateset/0002in.csv')
    print(max)
    print(min)
