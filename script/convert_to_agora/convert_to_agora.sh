#!/bin/bash

# sh convert_to_agora.sh

unamestr=`uname`

if [[ "$unamestr" = MINGW* || "$unamestr" = "Linux" ]]; then
    	SED="sed"
elif [ "$unamestr" = "Darwin" ]; then
    	SED="gsed"
else
    	echo "only Linux, MingW or Mac OS can be supported" &
    	exit 1
fi



echo "----------------- Start convert to agora -----------------"
# enter root dir
cd ../../
echo "当前路径：$(pwd)"
# 清空本地改动,切换到agora分支
git checkout -f ; git clean -fd ; git checkout dev; git branch -D dev-agora; git checkout -b dev-agora

echo "current branch :"
git branch
#更改包名及文件目录结构
python3 script/convert_to_agora/change_package_name.py ./
#对文件中的引用sdk类名进行国内->海外版本的替换
python3 script/convert_to_agora/rename_file_and_update_content.py ./ --replace-content


#处理settings.gradle.kts
$SED -i '/.*maven\.aliyun\.com.*/s/^/\/\// ' settings.gradle.kts
#处理Readme.md
$SED -i 's/io\.hyphenate\/ease-chat-kit/io\.agora\.rtc\/chat-uikit/g' README.md
$SED -i 's/io\.hyphenate\:ease-chat-kit/io\.agora\.rtc\:chat-uikit/g' README.md
$SED -i 's/io\.hyphenate\/ease-chat-kit/io\.agora\.rtc\/chat-uikit/g' README.zh.md
$SED -i 's/io\.hyphenate\:ease-chat-kit/io\.agora\.rtc\:chat-uikit/g' README.zh.md


git add . ; git commit -m "convert to agora"

echo "----------------- Finish convert to agora -----------------"





