package com.horical.appnote.Supports;

import android.content.Context;

import com.horical.appnote.LocalStorage.ApplicationSharedData;
import com.horical.appnote.R;

/**
 * Created by Phuong on 16/11/2015.
 */
public class LanguageUtils {
    public static String CURRENT_LANGUAGE = ApplicationSharedData.getLanguage();
    public static final String ENGLISH = "English", VIETNAMESE = "Tiếng Việt";

    private static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }

    public static void setCurrentLanguage(String language) {
        ApplicationSharedData.setLanguage(language);
        CURRENT_LANGUAGE = ApplicationSharedData.getLanguage();
    }

    public static String getCalendarString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Calendar_VI);
            default:
                return mContext.getResources().getString(R.string.Calendar_EN);
        }
    }
    public static String getAccountString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Account_VI);
            default:
                return mContext.getResources().getString(R.string.Account_EN);
        }
    }
    public static String getFileString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Files_VI);
            default:
                return mContext.getResources().getString(R.string.Files_EN);
        }
    }
    public static String getSettingString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Setting_VI);
            default:
                return mContext.getResources().getString(R.string.Setting_EN);
        }
    }

    public static String getNewNoteString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NewNote_VI);
            default:
                return mContext.getResources().getString(R.string.NewNote_EN);
        }
    }

    public static String getListNotesString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.ListNotes_VI);
            default:
                return mContext.getResources().getString(R.string.ListNotes_EN);
        }
    }

    public static String getFileManagerString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.FilesManager_VI);
            default:
                return mContext.getResources().getString(R.string.FilesManager_EN);
        }
    }

    public static String getViewString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.View_VI);
            default:
                return mContext.getResources().getString(R.string.View_EN);
        }
    }

    public static String getUpdateString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Update_VI);
            default:
                return mContext.getResources().getString(R.string.Update_EN);
        }
    }

    public static String getDeleteString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Delete_VI);
            default:
                return mContext.getResources().getString(R.string.Delete_EN);
        }
    }

    public static String getLanguageString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Language_VI);
            default:
                return mContext.getResources().getString(R.string.Language_EN);
        }
    }

    public static String getUseGuideString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.UseGuide_VI);
            default:
                return mContext.getResources().getString(R.string.UseGuide_EN);
        }
    }

    public static String getAutoSaveString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.AutoSave_VI);
            default:
                return mContext.getResources().getString(R.string.AutoSave_EN);
        }
    }

    public static String getImageString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Image_VI);
            default:
                return mContext.getResources().getString(R.string.Image_EN);
        }
    }

    public static String getAudioString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Audio_VI);
            default:
                return mContext.getResources().getString(R.string.Audio_EN);
        }
    }

    public static String getNothingListNoteString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NothingListNote_VI);
            default:
                return mContext.getResources().getString(R.string.NothingListNote_EN);
        }
    }

    public static String getFileTooBigString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.FileTooBig_VI);
            default:
                return mContext.getResources().getString(R.string.FileTooBig_EN);
        }
    }

    public static String getLastWeekString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.LastWeek_VI);
            default:
                return mContext.getResources().getString(R.string.LastWeek_EN);
        }
    }

    public static String getThisWeekString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.ThisWeek_VI);
            default:
                return mContext.getResources().getString(R.string.ThisWeek_EN);
        }
    }

    public static String getBannerMorningString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.bannerMorning_VI);
            default:
                return mContext.getResources().getString(R.string.bannerMorning_EN);
        }
    }

    public static String getBannerMorningWorkString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.bannerMorningWork_VI);
            default:
                return mContext.getResources().getString(R.string.bannerMorningWork_EN);
        }
    }

    public static String getBannerMiddayString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.bannerMidday_VI);
            default:
                return mContext.getResources().getString(R.string.bannerMidday_EN);
        }
    }

    public static String getBannerAfternoonString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.bannerAfternoon_VI);
            default:
                return mContext.getResources().getString(R.string.bannerAfternoon_EN);
        }
    }

    public static String getBannerEveningString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.bannerEvening_VI);
            default:
                return mContext.getResources().getString(R.string.bannerEvening_EN);
        }
    }

    public static String getBannerNightString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.bannerNight_VI);
            default:
                return mContext.getResources().getString(R.string.bannerNight_EN);
        }
    }

    public static String getNothingListFileString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NothingListFile_VI);
            default:
                return mContext.getResources().getString(R.string.NothingListFile_EN);
        }
    }

    public static String getProcessingString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Processing_VI);
            default:
                return mContext.getResources().getString(R.string.Processing_EN);
        }
    }

    public static String getSyncTitleString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.SyncTitle_VI);
            default:
                return mContext.getResources().getString(R.string.SyncTitle_EN);
        }
    }

    public static String getInternetOfflineString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.InternetOffline_VI);
            default:
                return mContext.getResources().getString(R.string.InternetOffline_EN);
        }
    }

    public static String getInternetOnlineString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.InternetOnline_VI);
            default:
                return mContext.getResources().getString(R.string.InternetOnline_EN);
        }
    }

    public static String getCreateNewString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.CreateNew_VI);
            default:
                return mContext.getResources().getString(R.string.CreateNew_EN);
        }
    }

    public static String getAttachString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Attach_VI);
            default:
                return mContext.getResources().getString(R.string.Attach_EN);
        }
    }

    public static String getTakePhotoString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.TakePhoto_VI);
            default:
                return mContext.getResources().getString(R.string.TakePhoto_EN);
        }
    }

    public static String getRecordAudioString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.RecordAudio_VI);
            default:
                return mContext.getResources().getString(R.string.RecordAudio_EN);
        }
    }

    public static String getNoteTitleHintString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NoteTitleHint_VI);
            default:
                return mContext.getResources().getString(R.string.NoteTitleHint_EN);
        }
    }

    public static String getNoteContentHintString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NoteContentHint_VI);
            default:
                return mContext.getResources().getString(R.string.NoteContentHint_EN);
        }
    }

    public static String getVideoCaptureString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.VideoCapture_VI);
            default:
                return mContext.getResources().getString(R.string.VideoCapture_EN);
        }
    }

    public static String getDateString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Date_VI);
            default:
                return mContext.getResources().getString(R.string.Date_EN);
        }
    }

    public static String getTimeString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Time_VI);
            default:
                return mContext.getResources().getString(R.string.Time_EN);
        }
    }

    public static String getBrowseVoiceString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.BrowseVoice_VI);
            default:
                return mContext.getResources().getString(R.string.BrowseVoice_EN);
        }
    }

    public static String getContentString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Content_VI);
            default:
                return mContext.getResources().getString(R.string.Content_EN);
        }
    }

    public static String getCancelString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Cancel_VI);
            default:
                return mContext.getResources().getString(R.string.Cancel_EN);
        }
    }

    public static String getCreateReminderString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.CreateReminder_VI);
            default:
                return mContext.getResources().getString(R.string.CreateReminder_EN);
        }
    }

    public static String getUsernameOrEmailString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.UsernameOrEmail_VI);
            default:
                return mContext.getResources().getString(R.string.UsernameOrEmail_EN);
        }
    }

    public static String getPasswordString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Password_VI);
            default:
                return mContext.getResources().getString(R.string.Password_EN);
        }
    }

    public static String getLoginString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Login_VI);
            default:
                return mContext.getResources().getString(R.string.Login_EN);
        }
    }

    public static String getForgotPasswordString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.ForgotPassword_VI);
            default:
                return mContext.getResources().getString(R.string.ForgotPassword_EN);
        }
    }

    public static String getSignUpString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.SignUp_VI);
            default:
                return mContext.getResources().getString(R.string.SignUp_EN);
        }
    }

    public static String getUserNameString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Username_VI);
            default:
                return mContext.getResources().getString(R.string.Username_EN);
        }
    }

    public static String getDisplayNameString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.DisplayName_VI);
            default:
                return mContext.getResources().getString(R.string.DisplayName_EN);
        }
    }

    public static String getChangePasswordString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.ChangePassword_VI);
            default:
                return mContext.getResources().getString(R.string.ChangePassword_EN);
        }
    }

    public static String getSaveChangesString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.SaveChange_VI);
            default:
                return mContext.getResources().getString(R.string.SaveChange_EN);
        }
    }

    public static String getOldPasswordString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.OldPassword_VI);
            default:
                return mContext.getResources().getString(R.string.OldPassword_EN);
        }
    }

    public static String getNewPasswordString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NewPassword_VI);
            default:
                return mContext.getResources().getString(R.string.NewPassword_EN);
        }
    }

    public static String getSelectActionString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.SelectAction_VI);
            default:
                return mContext.getResources().getString(R.string.SelectAction_EN);
        }
    }

    public static String getSelectAudioString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.SelectAudio_VI);
            default:
                return mContext.getResources().getString(R.string.SelectAudio_EN);
        }
    }

    public static String getNotifySameScreenString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NotifySameScreen_VI);
            default:
                return mContext.getResources().getString(R.string.NotifySameScreen_EN);
        }
    }

    public static String getNotifyPressAgainString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NotifyPressAgain_VI);
            default:
                return mContext.getResources().getString(R.string.NotifyPressAgain_EN);
        }
    }

    public static String getNotifySyncSuccessString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NotifySyncSuccess_VI);
            default:
                return mContext.getResources().getString(R.string.NotifySyncSuccess_EN);
        }
    }

    public static String getNotifySyncFailString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NotifySyncFail_VI);
            default:
                return mContext.getResources().getString(R.string.NotifySyncFail_EN);
        }
    }

    public static String getNotifyDownloadSuccessString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NotifyDownloadSuccess_VI);
            default:
                return mContext.getResources().getString(R.string.NotifyDownloadSuccess_EN);
        }
    }

    public static String getNotifyDownloadFailString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NotifyDownloadFail_VI);
            default:
                return mContext.getResources().getString(R.string.NotifyDownloadFail_EN);
        }
    }

    public static String getNotifyLimitString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NotifyLimit_VI);
            default:
                return mContext.getResources().getString(R.string.NotifyLimit_EN);
        }
    }

    public static String getNotifyNothingDownloadString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NotifyNothingDownload_VI);
            default:
                return mContext.getResources().getString(R.string.NotifyNothingDownload_EN);
        }
    }

    public static String getNotifyNothingUploadString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NotifyNothingUpload_VI);
            default:
                return mContext.getResources().getString(R.string.NotifyNothingUpload_EN);
        }
    }

    public static String getNotifyNothingDeleteString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NotifyNothingDelete_VI);
            default:
                return mContext.getResources().getString(R.string.NotifyNothingDelete_EN);
        }
    }

    public static String getNotifyFileExistedString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NotifyFileExisted_VI);
            default:
                return mContext.getResources().getString(R.string.NotifyFileExisted_EN);
        }
    }

    public static String getNotifyNoteExistedString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NotifyNoteExisted_VI);
            default:
                return mContext.getResources().getString(R.string.NotifyNoteExisted_EN);
        }
    }

    public static String getNotifyUploadSuccessString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NotifyUploadSuccess_VI);
            default:
                return mContext.getResources().getString(R.string.NotifyUploadSuccess_EN);
        }
    }

    public static String getNotifyUploadFailString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NotifyUploadFail_VI);
            default:
                return mContext.getResources().getString(R.string.NotifyUploadFail_EN);
        }
    }

    public static String getLoginSuccessString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.LoginSuccess_VI);
            default:
                return mContext.getResources().getString(R.string.LoginSuccess_EN);
        }
    }

    public static String getSignUpSuccessString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.SignUpSuccess_VI);
            default:
                return mContext.getResources().getString(R.string.SignUpSuccess_EN);
        }
    }

    public static String getWrongAccountString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.WrongAccount_VI);
            default:
                return mContext.getResources().getString(R.string.WrongAccount_EN);
        }
    }

    public static String getReminderSavedString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.ReminderSaved_VI);
            default:
                return mContext.getResources().getString(R.string.ReminderSaved_EN);
        }
    }

    public static String getReminderExistedString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.ReminderAddFailed_VI);
            default:
                return mContext.getResources().getString(R.string.ReminderAddFailed_EN);
        }
    }

    public static String getWrongCurrentPasswordString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.CurrentPasswordWrong_VI);
            default:
                return mContext.getResources().getString(R.string.CurrentPasswordWrong_EN);
        }
    }

    public static String getAccountExistedString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.AccountExisted_VI);
            default:
                return mContext.getResources().getString(R.string.AccountExisted_EN);
        }
    }

    public static String getPasswordChangeString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.PasswordChange_VI);
            default:
                return mContext.getResources().getString(R.string.PasswordChange_EN);
        }
    }

    public static String getNotifyCompletedString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NotifyCompleted_VI);
            default:
                return mContext.getResources().getString(R.string.NotifyCompleted_EN);
        }
    }

    public static String getNotifyFailedString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.NotifyFailed_VI);
            default:
                return mContext.getResources().getString(R.string.NotifyFailed_EN);
        }
    }

    public static String getDeleteFileSuccessString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.DeleteFileSuccess_VI);
            default:
                return mContext.getResources().getString(R.string.DeleteFileSuccess_EN);
        }
    }

    public static String getDeleteFileFailedString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.DeleteFileFail_VI);
            default:
                return mContext.getResources().getString(R.string.DeleteFileFail_EN);
        }
    }

    public static String getChooseMediaPlayerString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.SelectMediaPlayer_VI);
            default:
                return mContext.getResources().getString(R.string.SelectMediaPlayer_EN);
        }
    }

    public static String getAddReminderSuccessString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.AddReminderSuccess_VI);
            default:
                return mContext.getResources().getString(R.string.AddReminderSuccess_EN);
        }
    }

    public static String getAddReminderFailString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.AddReminderFail_VI);
            default:
                return mContext.getResources().getString(R.string.AddReminderFail_EN);
        }
    }

    public static String getMissFieldsString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.MissFields_VI);
            default:
                return mContext.getResources().getString(R.string.MissFields_EN);
        }
    }

    public static String getSelectFileString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.SelectFile_VI);
            default:
                return mContext.getResources().getString(R.string.SelectFile_EN);
        }
    }

    public static String getSelectFileErrorString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.LoadFileError_VI);
            default:
                return mContext.getResources().getString(R.string.LoadFileError_EN);
        }
    }

    public static String getSlideShowBegunString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.SlideShowBegun_VI);
            default:
                return mContext.getResources().getString(R.string.SlideShowBegun_EN);
        }
    }

    public static String getConfirmSaveString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.ConfirmSave_VI);
            default:
                return mContext.getResources().getString(R.string.ConfirmSave_EN);
        }
    }

    public static String getLoadImageErrorString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.LoadImageError_VI);
            default:
                return mContext.getResources().getString(R.string.LoadImageError_EN);
        }
    }

    public static String getLoadVideoErrorString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.LoadVideoError_VI);
            default:
                return mContext.getResources().getString(R.string.LoadVideoError_EN);
        }
    }

    public static String getEmptyNoteString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.EmptyNote_VI);
            default:
                return mContext.getResources().getString(R.string.EmptyNote_EN);
        }
    }

    public static String getEmptyListReminderString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.ListReminderEmpty_VI);
            default:
                return mContext.getResources().getString(R.string.ListReminderEmpty_EN);
        }
    }

    public static String getAddNewReminderString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.AddNewReminder_VI);
            default:
                return mContext.getResources().getString(R.string.AddNewReminder_EN);
        }
    }

    public static String getInsertNoteSuccessString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.InsertNoteSuccess_VI);
            default:
                return mContext.getResources().getString(R.string.InsertNoteSuccess_EN);
        }
    }

    public static String getInsertNoteFailString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.InsertNoteFail_VI);
            default:
                return mContext.getResources().getString(R.string.InsertNoteFail_EN);
        }
    }

    public static String getYesString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Yes_VI);
            default:
                return mContext.getResources().getString(R.string.Yes_EN);
        }
    }

    public static String getNoString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.No_VI);
            default:
                return mContext.getResources().getString(R.string.No_EN);
        }
    }

    public static String getOrString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Or_VI);
            default:
                return mContext.getResources().getString(R.string.Or_EN);
        }
    }

    public static String getDayOffString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.bannerDayOff_VI);
            default:
                return mContext.getResources().getString(R.string.bannerDayOff_EN);
        }
    }

    public static String getSundayString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.bannerSunday_VI);
            default:
                return mContext.getResources().getString(R.string.bannerSunday_EN);
        }
    }

    public static String getStartOfString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.StartOf_VI);
            default:
                return mContext.getResources().getString(R.string.StartOf_EN);
        }
    }

    public static String getEndOfString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.EndOf_VI);
            default:
                return mContext.getResources().getString(R.string.EndOf_EN);
        }
    }

    public static String getMonthString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Month_VI);
            default:
                return mContext.getResources().getString(R.string.Month_EN);
        }
    }

    public static String getWeekString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.Week_VI);
            default:
                return mContext.getResources().getString(R.string.Week_EN);
        }
    }

    public static String getSaturdayString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.bannerSaturday_VI);
            default:
                return mContext.getResources().getString(R.string.bannerSaturday_EN);
        }
    }

    public static String getYourNoteString() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return mContext.getResources().getString(R.string.YourNote_VI);
            default:
                return mContext.getResources().getString(R.string.YourNote_EN);
        }
    }

    public static String[] getSideMenu() {
        int resId = 0;
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                resId = R.array.Items_Vi;
                break;
            default:
                resId = R.array.Items_En;
                break;
        }
        return mContext.getResources().getStringArray(resId);
    }

    public static String[] getListNoteMenu() {
        int resId = 0;
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                resId = R.array.ListNoteContextItem_VI;
                break;
            default:
                resId = R.array.ListNoteContextItem_EN;
                break;
        }
        return mContext.getResources().getStringArray(resId);
    }

    public static int getMainMenuRes() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return R.menu.menu_main_activity_vi;
            default:
                return R.menu.menu_main_activity_en;
        }
    }

    public static int getCreateNoteMenuRes() {
        switch (CURRENT_LANGUAGE) {
            case VIETNAMESE:
                return R.menu.menu_create_note_vi;
            default:
                return R.menu.menu_create_note_en;
        }
    }

    public static String getMonthString(int month) {
        switch (month) {
            case 0:
                return (CURRENT_LANGUAGE.equals(ENGLISH))?"January":"Tháng 1";
            case 1:
                return (CURRENT_LANGUAGE.equals(ENGLISH))?"February":"Tháng 2";
            case 2:
                return (CURRENT_LANGUAGE.equals(ENGLISH))?"March":"Tháng 3";
            case 3:
                return (CURRENT_LANGUAGE.equals(ENGLISH))?"April":"Tháng 4";
            case 4:
                return (CURRENT_LANGUAGE.equals(ENGLISH))?"May":"Tháng 5";
            case 5:
                return (CURRENT_LANGUAGE.equals(ENGLISH))?"June":"Tháng 6";
            case 6:
                return (CURRENT_LANGUAGE.equals(ENGLISH))?"July":"Tháng 7";
            case 7:
                return (CURRENT_LANGUAGE.equals(ENGLISH))?"August":"Tháng 8";
            case 8:
                return (CURRENT_LANGUAGE.equals(ENGLISH))?"September":"Tháng 9";
            case 9:
                return (CURRENT_LANGUAGE.equals(ENGLISH))?"October":"Tháng 10";
            case 10:
                return (CURRENT_LANGUAGE.equals(ENGLISH))?"November":"Tháng 11";
            case 11:
                return (CURRENT_LANGUAGE.equals(ENGLISH))?"December":"Tháng 12";
        }
        return "";
    }

}
