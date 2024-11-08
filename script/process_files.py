# -*- coding: utf-8 -*-
import os
import sys

def process_files(directory):
    # 创建一个空字典来存储文件名和对应的值
    file_map = {}

    # 遍历目标文件夹及其所有子文件夹中的所有文件
    print(f"Scanning directory: {directory}")
    for root, _, files in os.walk(directory):
        for filename in files:
            full_path = os.path.join(root, filename)
            if filename.startswith('Ease') and filename.endswith('.kt'):
                # 处理以Ease开头且以.kt结尾的文件
                base_name = os.path.splitext(filename)[0]
                value = base_name.replace('Ease', 'UIKit', 1)
                file_map[base_name] = value
                print(f"Processed Ease file: {filename} -> {value}")
            elif filename.startswith('ease') and filename.endswith('.xml'):
                # 处理以ease开头且以.xml结尾的文件
                base_name = os.path.splitext(filename)[0]
                if base_name.startswith('ease_chat'):
                    value = base_name.replace('ease_chat', 'chat_im', 1)
                else:
                    value = base_name.replace('ease', 'chat', 1)
                file_map[base_name] = value
                print(f"Processed Ease file: {filename} -> {value}")
            elif filename.startswith('ease') and (filename.endswith('.png') or filename.endswith('.jpg')):
                # 处理以ease开头且以.png和jpg结尾的文件
                base_name = os.path.splitext(filename)[0]
                if base_name.startswith('ease_chat'):
                    value = base_name.replace('ease_chat', 'chat_im', 1)
                else:
                    value = base_name.replace('ease', 'chat', 1)
                file_map[base_name] = value
                print(f"Processed Ease file: {filename} -> {value}")
            else:
                print(f"Skipping file: {filename}")

    print(f"Total files processed: {len(file_map)}")
    return file_map

def write_map_to_file(file_map, output_file):
    # 检查result.txt文件是否存在，如果存在则删除
    if os.path.exists(output_file):
        print(f"{output_file} already exists. Deleting it.")
        os.remove(output_file)

    print(f"Writing results to {output_file}")
    with open(output_file, 'w') as f:
        for key, value in file_map.items():
            f.write(f"\"{key}\": \"{value}\",\n")
            print(f"Wrote to file: {key}: {value}")

def main(directory):
    # 输出文件路径
    output_file = 'result.txt'

    # 获取目标文件夹中的符合条件的文件
    file_map = process_files(directory)

    # 将字典写入到result.txt文件中
    write_map_to_file(file_map, output_file)

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Usage: python process_files.py <directory>")
        sys.exit(1)

    # 获取命令行参数中的目标文件夹路径
    target_directory = sys.argv[1]

    # 检查目标文件夹是否存在
    if not os.path.isdir(target_directory):
        print(f"Error: Directory {target_directory} does not exist.")
        sys.exit(1)

    # 调用main函数处理文件
    main(target_directory)
