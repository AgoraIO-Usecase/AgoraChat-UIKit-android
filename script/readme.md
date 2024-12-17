


## 1、本脚本用户将文件和文件内部引用内容更改：例如将ease_开头的文件名和文件内部引用内容更改为其他名称。

### 对于uikit
```shell
cd script
python3 rename_file_and_update_content.py ../ease-im-kit/src --rename-files --replace-content
python3 rename_file_and_update_content.py ../quickstart/src --rename-files --replace-content
```



### 对于demo或者其他调用工程:
```shell
python3 rename_file_and_update_content.py ../quickstart/src  --replace-content --rename-files
```

> 其他:
> 找出需要变更名称的文件： python3 process_files.py ../ease-im-kit/src
> 结果生成在result.txt中，然后拷贝到rename_file_and_update_content.py脚本里，实际上拷贝过去后，操作者会对生成的文件名根据实际情况略有微调。

> process_files.py 脚本写出来后第一次执行完后拿到了对应的需要重命名的map,以后就不需要再次执行了，放这里仅仅是为了备份一下。
> find_and_replace_viewbinding.py 找出需要替换的viewbinding文件，脚本放这里仅仅是为了备份一下
> find_ease_words.py 找出需要替换的ease_开头的string单词，脚本放这里仅仅是为了备份一下
> find_style_words.py 找出需要替换的viewbinding文件，脚本放这里仅仅是为了备份一下

## 2、convert_to_agora 用于将国内版本转换成海外版本，主要是更改包名及readme.md中的内容
```shell
cd script/convert_to_agora
sh convert_to_agora.sh
```
