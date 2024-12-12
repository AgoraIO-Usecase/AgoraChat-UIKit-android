# Changelog

## v1.2.0

### Added
* Add modify message feature.
* Add @ mention feature in group chat.
* Add forward messages feature.
* Add reply message feature.

### Fixed
* Fixed the issue where the voice message cannot be downloaded again after clicking the voice icon after the automatic download fails.

## v1.0.9(Mar 9, 2023)

### Improved
* Update Chat SDK version to 1.0.9.


## v1.0.8(Nov 3, 2022)

### Improved
* Update Chat SDK version to 1.0.8.


## v1.0.7(Sep 2, 2022)
### Added
* Add back button for ShowBigImageActivity.
* Add video extension function for ChatMessageListLayout.
* Add conversations do not disturb setting.

### Fixed
* Fix the problem that the left and right bubble settings are opposite.
* Fix the bug that EaseChatFragment cannot set the right drawable.
* Fix the bug that nickname is not displayed when conversation type is Conversation.ConversationType#Chat.

### Improved
* Replace of ActivityManager.getRunningAppProcesses().

## v1.0.6(Jun 16, 2022)

### Fixed
* Fix the bug which cannot get first character from username.

### Improved
* Optimize the view of Reactions.

## v1.0.5(May 30, 2022)
### Added
* Add `messageLongPressExtMenuItemArray` callback function with messageModel param

### Fixed
* Fix a crash while recall a timeString message cell
* Fix some UI operation not in main thread

### Improved
* Use EaseChatType enum to replace int type.
* Optimize init logic for chat thread fragment.
