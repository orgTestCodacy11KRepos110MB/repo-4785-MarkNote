package me.shouheng.notepal.util.listener;

import me.shouheng.data.entity.Attachment;

public interface OnAttachingFileListener {

    void onAttachingFileErrorOccurred(Attachment attachment);

    void onAttachingFileFinished(Attachment attachment);
}