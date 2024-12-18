import os
import sys
import shutil
import time

def replace_package_name(project_path, old_package, new_package):
 
    # 将包名转换为目录路径
    old_package_path = old_package.replace('.', os.sep)
    new_package_path = new_package.replace('.', os.sep)

    files_modified = 0
    dirs_deleted = 0

    # Step 1: Replace package name in files
    for root, dirs, files in os.walk(project_path):
        # Remove 'build' and '.gradle' directories
        for d in ['build', '.gradle']:
            dir_path = os.path.join(root, d)
            if os.path.exists(dir_path):
                shutil.rmtree(dir_path)
                dirs_deleted += 1

        # Skip 'build' and '.gradle' directories in traversal
        dirs[:] = [d for d in dirs if d not in ['build', '.gradle','.idea','gradle','.git']]
        
        for file in files:
            if file.endswith('.java') or file.endswith('.kt') or file.endswith('.xml') or file.endswith('.kts') \
            or file.endswith('.gradle') or file.endswith('.md'):
                file_path = os.path.join(root, file)
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                new_content = content.replace(old_package, new_package)
                if new_content != content:
                    with open(file_path, 'w', encoding='utf-8') as f:
                        f.write(new_content)
                    files_modified += 1
                    print(f"Modified file: {file_path}")
            if file.endswith('.kts') or file.endswith('.gradle'):
                file_path = os.path.join(root, file)
                replace_in_file(file_path, 'io.hyphenate:hyphenate-chat:4.11.0', 'io.agora.rtc:chat-sdk:1.3.1-beta')
 
    for dirpath, dirnames, filenames in os.walk(project_path):
        # Skip 'build' and '.gradle' directories in traversal
        dirnames[:] = [d for d in dirnames if d not in ['build', '.gradle','.idea','gradle','.git']]
        # 检查当前目录是否包含目标路径
        if old_package_path in dirpath:
            # 返回目标路径的父目录
            # 找到包含目标路径的目录，截取掉目标路径及其以后的部分
            parent_dir = dirpath.split(old_package_path)[0]
            target_path= parent_dir+new_package_path
            print(f"parent_dir={parent_dir}")
            print(f"source path={dirpath}")
            print(f"target path={target_path}")
            copy_and_remove_directory(dirpath,target_path)
            # 删除old_package_path目录
            shutil.rmtree(parent_dir+"/com")
        else :
            print(f"{old_package_path} not in ${dirpath}")

    

    return files_modified, dirs_deleted

def replace_in_file(file_path, original_text, replacement_text):
    """
    替换文件中的特定字符串。
    :param file_path: 文件路径
    :param original_text: 原始字符串
    :param replacement_text: 替换后的字符串
    """
    with open(file_path, 'r', encoding='utf-8') as file:
        file_data = file.read()

    file_data = file_data.replace(original_text, replacement_text)

    with open(file_path, 'w', encoding='utf-8') as file:
        file.write(file_data)

def copy_and_remove_directory(src_dir, dest_dir):
    # 如果目标目录存在，删除目标目录及其所有内容
    if os.path.exists(dest_dir):
        shutil.rmtree(dest_dir)
    
    # 创建目标目录
    os.makedirs(dest_dir)
    
    # 遍历源目录的所有文件和子目录
    for item in os.listdir(src_dir):
        src_path = os.path.join(src_dir, item)
        dest_path = os.path.join(dest_dir, item)
        
        if os.path.isdir(src_path):
            # 如果是目录，递归拷贝
            shutil.copytree(src_path, dest_path)
        else:
            # 如果是文件，直接拷贝
            shutil.copy2(src_path, dest_path)
    
    


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python change_package_name.py <project_path>")
        sys.exit(1)
    
    project_path = sys.argv[1]
    old_package = "com.hyphenate.easeui"
    new_package = "io.agora.chat.uikit"
    
    start_time = time.time()

    files_modified, dirs_deleted = replace_package_name(project_path, old_package, new_package)

    end_time = time.time()
    elapsed_time = end_time - start_time

    print(f"Package name changed from '{old_package}' to '{new_package}' in project '{project_path}'")
    print(f"Total files modified: {files_modified}")
    print(f"Total directories deleted: {dirs_deleted}")
    print(f"Total time taken: {elapsed_time:.2f} seconds")
