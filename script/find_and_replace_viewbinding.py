import os
import re
import json
import argparse

def find_and_replace_in_files(directory):
    # 定义正则表达式模式
    pattern = re.compile(r'\b(Ease\w*Binding)\b')

    # 用于存储结果的字典
    result_map = {}

    # 遍历指定文件夹及其子文件夹中的所有文件
    for root, _, files in os.walk(directory):
        for file in files:
            file_path = os.path.join(root, file)
            try:
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                    matches = pattern.findall(content)
                    for match in matches:
                        key = match
                        value = key.replace('Ease', 'Uikit', 1)
                        result_map[key] = value
            except (UnicodeDecodeError, FileNotFoundError):
                # 跳过无法读取的文件
                continue

    # 将结果以 JSON 格式写入 result.txt 文件
    with open('result.txt', 'w', encoding='utf-8') as f:
        json.dump(result_map, f, ensure_ascii=False, indent=4)

def main():
    # 解析命令行参数
    parser = argparse.ArgumentParser(description="Traverse a directory and replace words in files.")
    parser.add_argument("directory", type=str, help="The directory to traverse.")
    args = parser.parse_args()

    # 调用处理函数
    find_and_replace_in_files(args.directory)

if __name__ == "__main__":
    main()
