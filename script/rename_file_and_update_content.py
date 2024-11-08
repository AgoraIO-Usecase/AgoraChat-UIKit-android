# -*- coding: UTF-8 -*-
import os
import argparse
import re
from concurrent.futures import ThreadPoolExecutor, as_completed

# 调用：python3 rename_file_and_update_content.py ./target_folder --rename-files --replace-content
# directory：目标文件夹路径。
# --rename-files：如果指定此参数，则会执行文件重命名操作。
# --replace-content：如果指定此参数，则会执行文件内容替换操作。


# 预置的文件名映射（不包含文件后缀）
files_map = {
    #kotlin
    "EaseIM": "ChatUIKitClient",
    "EaseIMConfig": "ChatUIKitConfig",
    "EaseBaseViewModel": "ChatUIKitBaseViewModel",
    "EaseReactionUserListViewModel": "ChatUIKitReactionUserListViewModel",
    "EaseChatReactionViewModel": "ChatUIKitReactionViewModel",
    "EaseMessageReactionListViewModel": "ChatUIKitReactionListViewModel",
    "EaseChatViewModel": "ChatUIKitViewModel",
    "EaseMessageListViewModel": "ChatUIKitMessageListViewModel",
    "EaseGroupViewModel": "ChatUIKitGroupViewModel",
    "EaseChatHistoryViewModel": "ChatUIKitHistoryViewModel",
    "EaseContactListViewModel": "ChatUIKitContactListViewModel",
    "EaseSearchViewModel": "ChatUIKitSearchViewModel",
    "EaseChatMessageReplyViewModel": "ChatUIKitMessageReplyViewModel",
    "EaseConversationListViewModel": "ChatUIKitConversationListViewModel",
    "EaseNotificationViewModel": "ChatUIKitNotificationViewModel",
    "EaseChatThreadViewModel": "ChatUIKitThreadViewModel",
    "EaseConversationRepository": "ChatUIKitConversationRepository",
    "EaseChatManagerRepository": "ChatUIKitManagerRepository",
    "EaseSearchRepository": "ChatUIKitSearchRepository",
    "EaseNotificationRepository": "ChatUIKitNotificationRepository",
    "EaseChatThreadRepository": "ChatUIKitThreadRepository",
    "EaseReportRepository": "ChatUIKitReportRepository",
    "EaseGroupRepository": "ChatUIKitGroupRepository",
    "EaseContactListRepository": "ChatUIKitContactListRepository",
    "EaseSearchEditText": "ChatUIKitSearchEditText",
    "EaseSearchView": "ChatUIKitSearchView",
    "EaseWaveView": "ChatUIKitWaveView",
    "EaseTitleBar": "ChatUIKitTitleBar",
    "EaseArrowItemView": "ChatUIKitArrowItemView",
    "EaseSidebar": "ChatUIKitSidebar",
    "EaseCustomAvatarView": "ChatUIKitCustomAvatarView",
    "EaseFlowLayout": "ChatUIKitFlowLayout",
    "EaseSwitchItemView": "ChatUIKitSwitchItemView",
    "EaseDividerGridItemDecoration": "ChatUIKitDividerGridItemDecoration",
    # "EaseImageView": "ChatUIKitImageView",
    "EaseInputEditText": "ChatUIKitInputEditText",
    "EaseChatRowCombine": "ChatUIKitRowCombine",
    "EaseChatRowVideo": "ChatUIKitRowVideo",
    "EaseChatRowImage": "ChatUIKitRowImage",
    "EaseChatRowThreadUnknown": "ChatUIKitRowThreadUnknown",
    "EaseChatRowAlert": "ChatUIKitRowAlert",
    "EaseChatRowUserCard": "ChatUIKitRowUserCard",
    "EaseChatRowThreadNotify": "ChatUIKitRowThreadNotify",
    "EaseChatRowVoice": "ChatUIKitRowVoice",
    "EaseChatRowLocation": "ChatUIKitRowLocation",
    "EaseChatRowCustom": "ChatUIKitRowCustom",
    "EaseChatRowBigExpression": "ChatUIKitRowBigExpression",
    "EaseChatRowUnknown": "ChatUIKitRowUnknown",
    "EaseChatRowText": "ChatUIKitRowText",
    "EaseChatRowUnsent": "ChatUIKitRowUnsent",
    "EaseChatRow": "ChatUIKitRow",
    "EaseChatRowFile": "ChatUIKitRowFile",
    "EasePhotoView": "ChatUIKitPhotoView",
    "EaseGroupProfileProvider": "ChatUIKitGroupProfileProvider",
    "EaseUserProfileProvider": "ChatUIKitUserProfileProvider",
    "EaseEmojiconInfoProvider": "ChatUIKitEmojiconInfoProvider",
    "EaseCustomActivityRoute": "ChatUIKitCustomActivityRoute",
    "EaseSettingsProvider": "ChatUIKitSettingsProvider",
    "EaseIMCache": "ChatUIKitCache",
    "EaseError": "ChatUIKitError",
    "EaseConstant": "ChatUIKitConstant",
    "EaseIMClientImpl": "ChatUIKitClientImpl",
    "EaseAvatarShape": "ChatUIKitAvatarShape",
    "EaseListViewType": "ChatUIKitListViewType",
    "EaseGroupMemberType": "ChatUIKitGroupMemberType",
    "EaseTranslationLanguageType": "ChatUIKitTranslationLanguageType",
    "EaseChatFinishReason": "ChatUIKitFinishReason",
    "EaseCacheType": "ChatUIKitCacheType",
    "EaseReplyMap": "ChatUIKitReplyMap",
    "EaseFlowBus": "ChatUIKitFlowBus",
    "EaseImageUtils": "ChatUIKitImageUtils",
    "EaseVoiceLengthUtils": "ChatUIKitVoiceLengthUtils",
    "EaseCompat": "ChatUIKitCompat",
    "EaseFileUtils": "ChatUIKitFileUtils",
    "EaseProfile": "ChatUIKitProfile",
    "EaseUser": "ChatUIKitUser",
    "EaseBottomSheetChildHelper": "ChatUIKitBottomSheetChildHelper",
    "EaseContactBottomSheetFragment": "ChatUIKitContactBottomSheetFragment",
    "EaseAlertDialog": "ChatUIKitAlertDialog",
    "EaseBottomSheetContainerHelper": "ChatUIKitBottomSheetContainerHelper",
    "EaseNewChatBottomSheetFragment": "ChatUIKitNewBottomSheetFragment",
    "EaseDingMessageHelper": "ChatUIKitDingMessageHelper",
    "EasePreferenceManager": "ChatUIKitPreferenceManager",
    "EaseAtMessageHelper": "ChatUIKitAtMessageHelper",
    "EaseNotifier": "ChatUIKitNotifier",
    "EaseChatRowVoicePlayer": "ChatUIKitRowVoicePlayer",
    "EaseMenuFilterHelper": "ChatUIKitMenuFilterHelper",
    "EaseEmojiHelper": "ChatUIKitEmojiHelper",
    "EaseTimerHelper": "ChatUIKitTimerHelper",
    "EaseThreadNotifyHelper": "ChatUIKitThreadNotifyHelper",
    "EaseVoiceRecorder": "ChatUIKitVoiceRecorder",
    "EaseTitleBarHelper": "ChatUIKitTitleBarHelper",
    "EaseSystemMsgConfig": "ChatUIKitSystemMsgConfig",
    "EaseBottomMenuConfig": "ChatUIKitBottomMenuConfig",
    "EaseDetailMenuConfig": "ChatUIKitDetailMenuConfig",
    "EaseAvatarConfig": "ChatUIKitAvatarConfig",
    "EaseChatConfig": "ChatUIKitConfig",
    "EaseMultiDeviceEventConfig": "ChatUIKitMultiDeviceEventConfig",
    "EaseDateFormatConfig": "ChatUIKitDateFormatConfig",
    "EaseHeaderItemConfig": "ChatUIKitHeaderItemConfig",
    "EaseCustomMentionItemConfig": "ChatUIKitCustomMentionItemConfig",
    "EaseMenuDialog": "ChatUIKitMenuDialog",
    "EaseMenuAdapter": "ChatUIKitMenuAdapter",
    "EaseMenuPopupWindow": "ChatUIKitMenuPopupWindow",
    "EaseMenuHelper": "ChatUIKitMenuHelper",
    "EaseMenuItemView": "ChatUIKitMenuItemView",
    "EaseChatMenuHelper": "ChatUIKitChatMenuHelper",
    "EaseChatExtendMenuDialog": "ChatUIKitExtendMenuDialog",
    "EaseSelectPopAdapter": "ChatUIKitSelectPopAdapter",
    "EaseDefaultEmojiconDatas": "ChatUIKitDefaultEmojiconDatas",
    "EaseEmojiconGroupEntity": "ChatUIKitEmojiconGroupEntity",
    "EaseEvent": "ChatUIKitEvent",
    "EasePreview": "ChatUIKitPreview",
    "EaseGroupProfile": "ChatUIKitGroupProfile",
    "EaseMenuItem": "ChatUIKitMenuItem",
    "EaseConversation": "ChatUIKitConversation",
    "EaseEmojicon": "ChatUIKitEmojicon",
    "EaseReactionEmojiconEntity": "ChatUIKitReactionEmojiconEntity",
    "EaseCustomHeaderItem": "ChatUIKitCustomHeaderItem",
    "EaseSize": "ChatUIKitSize",
    "EasePager": "ChatUIKitPager",
    "EaseReaction": "ChatUIKitReaction",
    "EaseContactsListFragment": "ChatUIKitContactsListFragment",
    "EaseContactDetailsActivity": "ChatUIKitContactDetailsActivity",
    "EaseBlockListFragment": "ChatUIKitBlockListFragment",
    "EaseBlockListActivity": "ChatUIKitBlockListActivity",
    "EaseContactCheckActivity": "ChatUIKitContactCheckActivity",
    "EaseContactHeaderConfigBinding": "ChatUIKitContactHeaderConfigBinding",
    "EaseContactHeaderConfig": "ChatUIKitContactHeaderConfig",
    "EaseListViewHolderFactory": "ChatUIKitListViewHolderFactory",
    "EaseContactListAdapter": "ChatUIKitContactListAdapter",
    "EaseContactDetailItemAdapter": "ChatUIKitContactDetailItemAdapter",
    "EaseCustomHeaderAdapter": "ChatUIKitCustomHeaderAdapter",
    "EaseUserContactItem": "ChatUIKitUserContactItem",
    "EaseGroupMemberViewHolder": "ChatUIKitGroupMemberViewHolder",
    "EaseContactListLayout": "ChatUIKitContactListLayout",
    "EaseChatFragment": "UIKitChatFragment",
    "EaseNewChatFragment": "UIKitNewChatFragment",
    "EaseAddUserCardFragment": "ChatUIKitAddUserCardFragment",
    "EaseMessageMenuReactionView": "ChatUIKitMessageMenuReactionView",
    "EaseReactionUserListFragment": "ChatUIKitReactionUserListFragment",
    "EaseChatReactionsDialog": "ChatUIKitReactionsDialog",
    "EaseMessageReactionsDialog": "ChatUIKitMessageReactionsDialog",
    "EaseChatMessageReactionView": "ChatUIKitMessageReactionView",
    "EaseMessageReactionViewHolderFactory": "ChatUIKitMessageReactionViewHolderFactory",
    "EaseReactionUserAdapter": "ChatUIKitReactionUserAdapter",
    "EaseMessageReactionAdapter": "ChatUIKitMessageReactionAdapter",
    "EaseReactionUserPagerAdapter": "ChatUIKitReactionUserPagerAdapter",
    "EaseReactionAddViewHolder": "ChatUIKitReactionAddViewHolder",
    "EaseReactionDefaultViewHolder": "ChatUIKitReactionDefaultViewHolder",
    "EaseReactionMoreViewHolder": "ChatUIKitReactionMoreViewHolder",
    "EaseReactionNormalViewHolder": "ChatUIKitReactionNormalViewHolder",
    "EaseChatPinMessageListViewGroup": "ChatUIKitPinMessageListViewGroup",
    "EaseChatPinItemSpaceDecoration": "ChatUIKitPinItemSpaceDecoration",
    "EaseChatPinDefaultViewHolder": "ChatUIKitPinDefaultViewHolder",
    "EaseChatPinTextMessageViewHolder": "ChatUIKitPinTextMessageViewHolder",
    "EaseChatPinImageMessageViewHolder": "ChatUIKitPinImageMessageViewHolder",
    "EaseChatMessageTranslationView": "ChatUIKitMessageTranslationView",
    "EaseChatMessageItemConfig": "ChatUIKitMessageItemConfig",
    "EaseChatHistoryFragment": "ChatUIKitHistoryFragment",
    "EaseChatHistoryLayout": "ChatUIKitHistoryLayout",
    "EaseChatHistoryAdapter": "ChatUIKitHistoryAdapter",
    "EaseChatRowHistoryImage": "ChatUIKitRowHistoryImage",
    "EaseChatRowHistoryVideo": "ChatUIKitRowHistoryVideo",
    "EaseChatRowHistoryUserCard": "ChatUIKitRowHistoryUserCard",
    "EaseChatRowHistoryVoice": "ChatUIKitRowHistoryVoice",
    "EaseChatRowHistoryLocation": "ChatUIKitRowHistoryLocation",
    "EaseChatRowHistoryText": "ChatUIKitRowHistoryText",
    "EaseChatRowHistoryFile": "ChatUIKitRowHistoryFile",
    "EaseChatRowHistoryBigExpression": "ChatUIKitRowHistoryBigExpression",
    "EaseChatRowHistoryCombine": "ChatUIKitRowHistoryCombine",
    "EaseChatHistoryViewHolderFactory": "ChatUIKitHistoryViewHolderFactory",
    "EaseHistoryImageViewHolder": "ChatUIKitHistoryImageViewHolder",
    "EaseHistoryVideoViewHolder": "ChatUIKitHistoryVideoViewHolder",
    "EaseHistoryUserCardViewHolder": "ChatUIKitHistoryUserCardViewHolder",
    "EaseHistoryExpressionViewHolder": "ChatUIKitHistoryExpressionViewHolder",
    "EaseHistoryFileViewHolder": "ChatUIKitHistoryFileViewHolder",
    "EaseHistoryVoiceViewHolder": "ChatUIKitHistoryVoiceViewHolder",
    "EaseHistoryCombineViewHolder": "ChatUIKitHistoryCombineViewHolder",
    "EaseHistoryTextViewHolder": "ChatUIKitHistoryTextViewHolder",
    "EaseLoadDataType": "ChatUIKitLoadDataType",
    "EaseReactionType": "ChatUIKitReactionType",
    "EaseChatType": "ChatUIKitType",
    "EaseChatExtendMenuIndicatorAdapter": "ChatUIKitExtendMenuIndicatorAdapter",
    "EaseMessagesAdapter": "ChatUIKitMessagesAdapter",
    "EaseChatPinMessageListAdapter": "ChatUIKitPinMessageListAdapter",
    "EaseChatEmojiGridAdapter": "ChatUIKitEmojiGridAdapter",
    "EaseChatExtendMenuAdapter": "ChatUIKitExtendMenuAdapter",
    "EaseChatMessageUrlPreview": "ChatUIKitMessageUrlPreview",
    "EaseMessageSearchResultFragment": "ChatUIKitMessageSearchResultFragment",
    "EaseChatMessageReplyView": "ChatUIKitMessageReplyView",
    "EaseChatExtendMessageReplyView": "ChatUIKitExtendMessageReplyView",
    "EaseUnknownViewHolder": "ChatUIKitUnknownViewHolder",
    "EaseVoiceViewHolder": "ChatUIKitVoiceViewHolder",
    "EaseMessageViewType": "ChatUIKitMessageViewType",
    "EaseCombineViewHolder": "ChatUIKitCombineViewHolder",
    "EaseCustomViewHolder": "ChatUIKitCustomViewHolder",
    "EaseChatViewHolderFactory": "ChatUIKitViewHolderFactory",
    "EaseThreadNotifyViewHolder": "ChatUIKitThreadNotifyViewHolder",
    "EaseUserCardViewHolder": "ChatUIKitUserCardViewHolder",
    "EaseChatRowViewHolder": "ChatUIKitRowViewHolder",
    "EaseExpressionViewHolder": "ChatUIKitExpressionViewHolder",
    "EaseUnsentViewHolder": "ChatUIKitUnsentViewHolder",
    "EaseVideoViewHolder": "ChatUIKitVideoViewHolder",
    "EaseAlertViewHolder": "ChatUIKitAlertViewHolder",
    "EaseTextViewHolder": "ChatUIKitTextViewHolder",
    "EaseImageViewHolder": "ChatUIKitImageViewHolder",
    "EaseFileViewHolder": "ChatUIKitFileViewHolder",
    "EaseThreadUnKnownViewHolder": "ChatUIKitThreadUnKnownViewHolder",
    "EaseMessageSearchResultActivity": "ChatUIKitMessageSearchResultActivity",
    "EaseShowLocalVideoActivity": "ChatUIKitShowLocalVideoActivity",
    "EaseShowBigImageActivity": "ChatUIKitShowBigImageActivity",
    "EaseChatActivity": "UIKitChatActivity",
    "EaseChatHistoryActivity": "ChatUIKitHistoryActivity",
    "EaseShowNormalFileActivity": "ChatUIKitShowNormalFileActivity",
    "EaseShowVideoActivity": "ChatUIKitShowVideoActivity",
    "EaseReportSheetDialog": "ChatUIKitReportSheetDialog",
    "EaseChatAttachmentController": "ChatUIKitAttachmentController",
    "EaseChatPinMessageController": "ChatUIKitPinMessageController",
    "EaseChatMessageReplyController": "ChatUIKitMessageReplyController",
    "EaseChatMessageMultipleSelectController": "ChatUIKitMessageMultipleSelectController",
    "EaseChatDialogController": "ChatUIKitDialogController",
    "EaseChatMessageReportController": "ChatUIKitMessageReportController",
    "EaseChatMentionController": "ChatUIKitMentionController",
    "EaseChatMessageEditController": "ChatUIKitMessageEditController",
    "EaseChatMessageTranslationController": "ChatUIKitMessageTranslationController",
    "EaseChatMessageListScrollAndDataController": "ChatUIKitMessageListScrollAndDataController",
    "EaseChatNotificationController": "ChatUIKitNotificationController",
    "EaseChatAddExtendFunctionViewController": "ChatUIKitAddExtendFunctionViewController",
    "EaseEmojiconPagerView": "ChatUIKitEmojiconPagerView",
    "EaseInputMenuStyle": "ChatUIKitInputMenuStyle",
    "EaseChatLayout": "ChatUIKitLayout",
    "EaseChatNotificationView": "ChatUIKitNotificationView",
    "EaseChatExtendMenu": "ChatUIKitExtendMenu",
    "EaseCustomLayoutManager": "ChatUIKitCustomLayoutManager",
    "EaseEmojiconIndicatorView": "ChatUIKitEmojiconIndicatorView",
    "EaseUnreadNotificationView": "ChatUIKitUnreadNotificationView",
    "EaseChatInputMenu": "ChatUIKitInputMenu",
    "EaseEmojiScrollTabBar": "ChatUIKitEmojiScrollTabBar",
    "EaseEmojiconMenu": "ChatUIKitEmojiconMenu",
    "EaseChatVoiceRecorderDialog": "ChatUIKitVoiceRecorderDialog",
    "EaseChatMessageListLayout": "ChatUIKitMessageListLayout",
    "EaseChatPrimaryMenu": "ChatUIKitPrimaryMenu",
    "EaseMessageForwardDialogFragment": "ChatUIKitMessageForwardDialogFragment",
    "EaseGroupListForwardFragment": "ChatUIKitGroupListForwardFragment",
    "EaseContactForwardFragmentEvent": "ChatUIKitContactForwardFragmentEvent",
    "EaseMessageForwardPagerAdapter": "ChatUIKitMessageForwardPagerAdapter",
    "EaseContactForwardAdapter": "ChatUIKitContactForwardAdapter",
    "EaseGroupListForwardAdapter": "ChatUIKitGroupListForwardAdapter",
    "EaseSearchForwardUserDialogFragment": "ChatUIKitSearchForwardUserDialogFragment",
    "EaseChatMessageMultiSelectHelper": "ChatUIKitMessageMultiSelectHelper",
    "EaseGroupForwardViewHolder": "ChatUIKitGroupForwardViewHolder",
    "EaseContactForwardViewHolder": "ChatUIKitContactForwardViewHolder",
    "EaseChatMultipleSelectMenuView": "ChatUIKitMultipleSelectMenuView",
    "EaseChatExtendMenuItemClickListener": "ChatUIKitExtendMenuItemClickListener",
    "EaseChatPrimaryMenuListener": "ChatUIKitPrimaryMenuListener",
    "EaseEmojiconMenuListener": "ChatUIKitEmojiconMenuListener",
    "EaseGroupMembersListActivity": "ChatUIKitGroupMembersListActivity",
    "EaseGroupMentionBottomSheet": "ChatUIKitGroupMentionBottomSheet",
    "EaseGroupDetailActivity": "ChatUIKitGroupDetailActivity",
    "EaseGroupListActivity": "ChatUIKitGroupListActivity",
    "EaseCreateGroupActivity": "ChatUIKitCreateGroupActivity",
    "EaseGroupDetailEditActivity": "ChatUIKitGroupDetailEditActivity",
    "EaseGroupListFragment": "ChatUIKitGroupListFragment",
    "EaseGroupAddMemberFragment": "ChatUIKitGroupAddMemberFragment",
    "EaseGroupRemoveMemberFragment": "ChatUIKitGroupRemoveMemberFragment",
    "EaseGroupMemberFragment": "ChatUIKitGroupMemberFragment",
    "EaseGroupListConfigBinding": "ChatUIKitGroupListConfigBinding",
    "EaseGroupListConfig": "ChatUIKitGroupListConfig",
    "EaseGroupSelectListAdapter": "ChatUIKitGroupSelectListAdapter",
    "EaseGroupListAdapter": "ChatUIKitGroupListAdapter",
    "EaseGroupMemberListAdapter": "ChatUIKitGroupMemberListAdapter",
    "EaseSelectContactViewHolder": "ChatUIKitSelectContactViewHolder",
    "EaseGroupListViewHolder": "ChatUIKitGroupListViewHolder",
    "EaseSearchUserFragment": "ChatUIKitSearchUserFragment",
    "EaseSearchSelectUserFragment": "ChatUIKitSearchSelectUserFragment",
    "EaseSearchConversationFragment": "ChatUIKitSearchConversationFragment",
    "EaseSearchActivity": "ChatUIKitSearchActivity",
    "EaseSearchForwardUserFragment": "ChatUIKitSearchForwardUserFragment",
    "EaseSearchMessageFragment": "ChatUIKitSearchMessageFragment",
    "EaseSearchMessageAdapter": "ChatUIKitSearchMessageAdapter",
    "EaseSearchUserAdapter": "ChatUIKitSearchUserAdapter",
    "EaseSearchConversationAdapter": "ChatUIKitSearchConversationAdapter",
    "EaseNewRequestsActivity": "ChatUIKitNewRequestsActivity",
    "EaseNewRequestsDetailsActivity": "ChatUIKitNewRequestsDetailsActivity",
    "EaseRequestAdapter": "ChatUIKitRequestAdapter",
    "EaseNewRequestsViewHolder": "ChatUIKitNewRequestsViewHolder",
    "EaseNotificationMsgManager": "ChatUIKitNotificationMsgManager",
    "EaseCreateChatThreadActivity": "ChatUIKitCreateThreadActivity",
    "EaseChatThreadActivity": "ChatUIKitThreadActivity",
    "EaseChatThreadMemberActivity": "ChatUIKitThreadMemberActivity",
    "EaseChatThreadListActivity": "ChatUIKitThreadListActivity",
    "EaseChatThreadListAdapter": "ChatUIKitThreadListAdapter",
    "EaseChatThreadListFragment": "ChatUIKitThreadListFragment",
    "EaseChatThreadMemberFragment": "ChatUIKitThreadMemberFragment",
    "EaseChatThreadFragment": "ChatUIKitThreadFragment",
    "EaseCreateChatThreadFragment": "ChatUIKitCreateThreadFragment",
    "EaseChatThreadAttachmentController": "ChatUIKitThreadAttachmentController",
    "EaseChatThreadController": "ChatUIKitThreadController",
    "EaseChatThreadListViewHolder": "ChatUIKitThreadListViewHolder",
    "EaseChatThreadRole": "ChatUIKitThreadRole",
    "EaseChatMessageThreadView": "ChatUIKitMessageThreadView",
    "EaseConversationListFragment": "ChatUIKitConversationListFragment",
    "EaseConvItemConfig": "ChatUIKitConvItemConfig",
    "EaseConvItemConfigBinding": "ChatUIKitConvItemConfigBinding",
    "EaseConversationListAdapter": "ChatUIKitConversationListAdapter",
    "EaseConversationViewHolderFactory": "ChatUIKitConversationViewHolderFactory",
    "EaseConvViewType": "ChatUIKitConvViewType",
    "EaseConversationViewHolder": "ChatUIKitConversationViewHolder",
    "EaseConvDialogController": "ChatUIKitConvDialogController",
    "EaseConversationListLayout": "ChatUIKitConversationListLayout",
    "EaseBaseSearchFragment": "ChatUIKitBaseSearchFragment",
    "EaseBaseSheetFragmentDialog": "ChatUIKitBaseSheetFragmentDialog",
    "EaseBaseActivity": "ChatUIKitBaseActivity",
    "EaseBaseAdapter": "ChatUIKitBaseAdapter",
    "EaseContainChildBottomSheetFragment": "ChatUIKitContainChildBottomSheetFragment",
    "EaseBaseFullDialogFragment": "ChatUIKitBaseFullDialogFragment",
    "EaseBaseListFragment": "ChatUIKitBaseListFragment",
    "EaseBaseFragment": "ChatUIKitBaseFragment",
    "EaseBaseRecyclerViewAdapter": "ChatUIKitBaseRecyclerViewAdapter",
    "EaseBaseChatExtendMenuAdapter": "ChatUIKitBaseExtendMenuAdapter",
    "EaseMessageListener": "ChatUIKitMessageListener",
    "EaseMultiDeviceListener": "ChatUIKitMultiDeviceListener",
    "EaseChatRoomListener": "UIKitChatRoomListener",
    "EaseConversationListener": "ChatUIKitConversationListener",
    "EaseContactListener": "ChatUIKitContactListener",
    "EaseIMClient": "IChatUIKitClient",
    "EaseConnectionListener": "ChatUIKitConnectionListener",
    "EaseGroupListener": "ChatUIKitGroupListener",

}
# 预置的字符串映射
string_map = files_map

#特殊处理 例如.9.png等
#string_map["ease_show_head_toast_bg"] = "chat_show_head_toast_bg"
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
    with open(file_path, 'r') as f:
        lines = f.readlines()

    new_lines = []
    modified = False
    for line_num, line in enumerate(lines, start=1):
        # 如果行中包含 'R.styleable.'，跳过替换
        if 'R.styleable.' in line:
            new_lines.append(line)
            continue

        new_line = line
        for old_string, new_string in string_map.items():
            pattern = r'\b' + re.escape(old_string) + r'\b'
            new_line = re.sub(pattern, new_string, new_line)
        if new_line != line:
            modified = True
        new_lines.append(new_line)

    if modified:
        with open(file_path, 'w') as f:
            f.writelines(new_lines)
        print(f'Modified content in: {file_path}')
        return 1
    return 0

def do_replace_content(root_dir, string_map, max_workers=8):
    modify_count = 0
    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = []
        for dirpath, dirnames, filenames in os.walk(root_dir):
            for filename in filenames:
                name, ext = os.path.splitext(filename)
                if ext not in ['.xml', '.kt', '.java']:
                    print(f"Skipping file with unsupported extension: {filename}")
                    continue
                if ext == '.xml' and 'layout' not in dirpath:
                    print(f"Skipping XML file not in layout directory: {filename}")
                    continue
                file_path = os.path.join(dirpath, filename)
                futures.append(executor.submit(process_file, file_path, string_map))
        for future in as_completed(futures):
            modify_count += future.result()
    return modify_count

def main(directory, rename_files, replace_content):
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

    print(f"Total files renamed: {files_renamed_count}")
    print(f"Total files with content changed: {files_content_changed_count}")

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Rename files and replace content in a directory.")
    parser.add_argument('directory', type=str, help='The target directory to process.')
    parser.add_argument('--rename-files', action='store_true', help='Rename files according to the predefined mapping.')
    parser.add_argument('--replace-content', action='store_true', help='Replace content in files according to the predefined mapping.')

    args = parser.parse_args()

    main(args.directory, args.rename_files, args.replace_content)
