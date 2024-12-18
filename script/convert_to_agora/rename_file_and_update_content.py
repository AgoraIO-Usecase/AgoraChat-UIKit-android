# -*- coding: UTF-8 -*-
import os
import argparse
import re
import time
from concurrent.futures import ThreadPoolExecutor, as_completed

# 调用：python3 rename_file_and_update_content.py ./target_folder --rename-files --replace-content
# directory：目标文件夹路径。
# --rename-files：如果指定此参数，则会执行文件重命名操作。
# --replace-content：如果指定此参数，则会执行文件内容替换操作。


# 预置的文件名映射（不包含文件后缀）
files_map = {

}

#需要替换的字符串map
extra_strings_map = {
    "com.hyphenate": "io.agora",
    "internal.com.getkeepsafe.relinker": "inner.com.getkeepsafe.relinker",
    "EMClient" : "ChatClient",
    "EMOptions" : "ChatOptions",
    "EMCallBack" : "CallBack",
    "EMResultCallBack":"ResultCallBack",
    "EMChatRoomChangeListener" : "ChatRoomChangeListener",
    "EMClientListener" : "ChatClientListener",
    "EMConnectionListener" : "ConnectionListener",
    "EMConversationListener" : "ConversationListener",
    "EMContactListener" : "ContactListener",
    "EMError" : "Error",
    "EMGroupChangeListener" : "GroupChangeListener",
    "EMMessageListener" : "MessageListener",
    "EMMultiDeviceListener" : "MultiDeviceListener",
    "EMValueCallBack" : "ValueCallBack",
    "EMChatManager" : "ChatManager",
    "EMChatRoom" : "ChatRoom",
    "EMChatRoomManager" : "ChatRoomManager",
    "EMCheckType" : "ChatCheckType",
    "EMCmdMessageBody" : "CmdMessageBody",
    "EMContactManager" : "ContactManager",
    "EMConversation" : "Conversation",
    "EMConversationType" : "ConversationType",
    "EMSearchDirection" : "SearchDirection",
    "EMCursorResult" : "CursorResult",
    "EMCustomMessageBody" : "CustomMessageBody",
    "EMDeviceInfo" : "DeviceInfo",
    "EMFileMessageBody" : "FileMessageBody",
    "EMGroup" : "Group",
    "EMGroupInfo" : "GroupInfo",
    "EMGroupManager" : "GroupManager",
    "EMGroupOptions" : "GroupOptions",
    "EMGroupReadAck" : "GroupReadAck",
    "EMImageMessageBody" : "ImageMessageBody",
    "EMLocationMessageBody" : "LocationMessageBody",
    "EMMessageBody" : "MessageBody",
    "EMMucSharedFile" : "MucSharedFile",
    "EMNormalFileMessageBody" : "NormalFileMessageBody",
    "EMPageResult" : "PageResult",
    "EMPushConfigs" : "PushConfigs",
    "EMPushManager" : "PushManager",
    "EMTextMessageBody" : "TextMessageBody",

    "EMCombineMessageBody" : "CombineMessageBody",

    "EMUserInfo" : "UserInfo",
    "EMUserInfoType" : "UserInfoType",
    "EMUserInfoManager" : "UserInfoManager",
    "EMVideoMessageBody" : "VideoMessageBody",
    "EMVoiceMessageBody" : "VoiceMessageBody",
    "HyphenateException" : "ChatException",
    "EMPushConfig" : "PushConfig",
    "EMPushHelper" : "PushHelper",
    "EMPushType" : "PushType",
    "EMFileHelper" : "FileHelper",
    "EMGroupPermissionType" : "GroupPermissionType",
    "EMGroupStyle" : "GroupStyle",
    "EMGroupStylePrivateOnlyOwnerInvite" : "GroupStylePrivateOnlyOwnerInvite",
    "EMGroupStylePrivateMemberCanInvite" : "GroupStylePrivateMemberCanInvite",
    "EMGroupStylePublicJoinNeedApproval" : "GroupStylePublicJoinNeedApproval",
    "EMGroupStylePublicJoinNeedApproval" : "GroupStylePublicJoinNeedApproval",
    "EMChatRoomPermissionType" : "ChatRoomPermissionType",
    "EMChatService" : "ChatService",
    "EMJobService" : "ChatJobService",
    "EMMonitorReceiver" : "MonitorReceiver",
    "EMMzMsgReceiver" : "MzMsgReceiver",
    "EMVivoMsgReceiver" : "VivoMsgReceiver",

    "EMLanguage" : "Language",
    "EMTranslateParams" : "TranslateParams",
    "EMTranslationManager" : "TranslationManager",
    "EMTranslationResult" : "TranslationResult",
    "EMTranslator" : "Translator",
    "EMTranslationInfo" : "TranslationInfo",

    "EMPresence" : "Presence",
    "EMPresenceManager" : "PresenceManager",
    "EMPresenceListener" : "PresenceListener",
    "EMSilentModeParam" : "SilentModeParam",
    "EMSilentModeResult" : "SilentModeResult",
    "EMSilentModeTime" : "SilentModeTime",
    "EMPushRemindType" : "PushRemindType",
    "EMSilentModeParamType" : "SilentModeParamType",

    "EMMessageReaction":"MessageReaction",
    "EMMessageReactionChange":"MessageReactionChange",

    "EMChatThreadEvent" : "ChatThreadEvent",
    "EMChatThreadManager" : "ChatThreadManager",
    "EMChatThreadChangeListener" : "ChatThreadChangeListener",

    "EMLogListener" : "ChatLogListener",

    "EMStatisticsManager" : "ChatStatisticsManager",
    "EMMessageStatistics" : "MessageStatistics",
    "EMSearchMessageDirect" : "SearchMessageDirect",
    "EMSearchMessageType" : "SearchMessageType",
    "EMFetchMessageOption" : "FetchMessageOption",

    "EMContact" : "Contact",
    "EMConversationFilter" : "ConversationFilter",
    "EMMessagePinInfo" : "MessagePinInfo",

   "EMRecallMessageInfo" : "RecallMessageInfo",
   "EMLoginExtensionInfo" : "LoginExtensionInfo",
   "EMMessage" : "ChatMessage",
    "EMChatThread" : "ChatThread",
    "EMMessageReactionOperation" : "MessageReactionOperation",
    "EMMessageSearchScope" : "ChatMessageSearchScope",

}
# 预置的字符串映射
string_map = files_map.copy()
string_map.update(extra_strings_map)



#特殊处理 例如.9.png等
# string_map["ease_show_head_toast_bg"] = "chat_show_head_toast_bg"
#string_map["ease_slidetab_bg_press"] = "chat_slidetab_bg_press"


def do_rename_files(root_dir, files_map):
    rename_count = 0
    for dirpath, dirnames, filenames in os.walk(root_dir):
        for filename in filenames:
            name, ext = os.path.splitext(filename)
            if name in files_map:
                old_path = os.path.join(dirpath, filename)
                new_filename = files_map[name] + ext
                new_path = os.path.join(dirpath, new_filename)
                os.rename(old_path, new_path)
                print(f'Renamed: {old_path} to {new_path}')
                rename_count += 1
    return rename_count

def process_file(file_path, string_map):
    # 按行替换
    # with open(file_path, 'r') as f:
    #     lines = f.readlines()
    #
    # new_lines = []
    # modified = False
    # for line_num, line in enumerate(lines, start=1):
    #     # 如果行中包含 'R.styleable.'，跳过替换
    #     if 'R.styleable.' in line:
    #         new_lines.append(line)
    #         continue
    #
    #     new_line = line
    #     for old_string, new_string in string_map.items():
    #         pattern = r'\b' + re.escape(old_string) + r'\b'
    #         new_line = re.sub(pattern, new_string, new_line)
    #     if new_line != line:
    #         modified = True
    #     new_lines.append(new_line)
    #
    # if modified:
    #     with open(file_path, 'w') as f:
    #         f.writelines(new_lines)
    #     print(f'Modified content in: {file_path}')
    #     return 1

    #整体替换，加快脚本速度,会导致R.styleable.EaseChatMessageReplyView->R.styleable.ChatUIKitMessageReplyView
    with open(file_path, 'r') as file:
        old_content = file.read()

    new_content=old_content
        # Use regex to replace only whole words
    for old_string, new_string in string_map.items():
            pattern = r'\b' + re.escape(old_string) + r'\b'
            new_content = re.sub(pattern, new_string, new_content)
    if new_content != old_content:
        with open(file_path, 'w') as file:
            file.write(new_content)
        print(f'Modified content in: {file_path}')
        return 1
    return 0

def do_replace_content(root_dir, string_map, max_workers=None):
    modify_count = 0
    if max_workers is None:
        max_workers = os.cpu_count()*2  # 推荐的最大线程数
        #打印空行
        print("\n")
        print(f"------------Using {max_workers} workers.---------------")
        print("\n")
    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = []
        for dirpath, dirnames, filenames in os.walk(root_dir):
            for filename in filenames:
                name, ext = os.path.splitext(filename)
                if ext not in ['.xml', '.kt', '.java','.kts','.gradle','.md']:
                    print(f"Skipping file with unsupported extension: {filename}")
                    continue
                # if ext == '.xml' and 'layout' not in dirpath:
                #     print(f"Skipping XML file not in layout directory: {filename}")
                #     continue
                file_path = os.path.join(dirpath, filename)
                futures.append(executor.submit(process_file, file_path, string_map))
        for future in as_completed(futures):
            modify_count += future.result()
    return modify_count

def main(directory, rename_files, replace_content):
    start_time = time.time()  # 记录开始时间
    if not os.path.isdir(directory):
        print(f"Error: Directory {directory} does not exist.")
        return

    files_renamed_count = 0
    files_content_changed_count = 0

    if rename_files:
        print("Starting file renaming process...")
        files_renamed_count = do_rename_files(directory, files_map)


    if replace_content:
        print("Starting file content replacement process...")
        files_content_changed_count = do_replace_content(directory, string_map)

    end_time = time.time()  # 记录结束时间
    total_time = end_time - start_time  # 计算总耗时
    # 将总耗时转换为分秒格式
    minutes, seconds = divmod(total_time, 60)

    print(f"Total files renamed: {files_renamed_count}")
    print(f"Total files with content changed: {files_content_changed_count}")
    print(f"Total time taken: {int(minutes)} minutes and {seconds:.2f} seconds")  # 打印总耗时

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Rename files and replace content in a directory.")
    parser.add_argument('directory', type=str, help='The target directory to process.')
    parser.add_argument('--rename-files', action='store_true', help='Rename files according to the predefined mapping.')
    parser.add_argument('--replace-content', action='store_true', help='Replace content in files according to the predefined mapping.')

    args = parser.parse_args()

    main(args.directory, args.rename_files, args.replace_content)
