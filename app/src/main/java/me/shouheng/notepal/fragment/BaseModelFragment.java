package me.shouheng.notepal.fragment;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ViewDataBinding;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.ContentActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.model.Model;
import me.shouheng.notepal.provider.BaseStore;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.widget.FlowLayout;


/**
 * Created by wangshouheng on 2017/9/3.*/
public abstract class BaseModelFragment<T extends Model, V extends ViewDataBinding> extends CommonFragment<V> {

    private Boolean isNewModel;
    private boolean contentChanged;
    private boolean savedOrUpdated;


    protected abstract boolean checkInputInfo();

    protected void beforeSaveOrUpdate(){}

    protected abstract void saveModel();

    protected abstract void updateModel();

    protected abstract BaseStore getStoreOfModel();

    protected abstract T getModel();

    protected void afterSaveOrUpdate() {}

    protected void setContentChanged() {
        this.contentChanged = true;
    }

    protected boolean isContentChanged() {
        return contentChanged;
    }

    protected boolean isNewModel() {
        if (isNewModel == null) {
            isNewModel = getStoreOfModel().isNewModel(getModel().getCode());
        }
        return isNewModel;
    }

    protected boolean saveOrUpdateData() {
        // 需要对输入的信息进行校验
        if (!checkInputInfo()){
            return false;
        }

        // 在持久化之前进行的操作
        beforeSaveOrUpdate();

        // 进行持久化操作
        if (isNewModel()){
            saveModel();
        } else {
            updateModel();
        }
        ToastUtils.makeToast(getContext(), R.string.text_save_successfully);

        // 重置页面内的编辑信息
        resetEditState();

        // 完成了持久化之后的其他操作
        afterSaveOrUpdate();
        return true;
    }

    private void resetEditState() {
        // 清除标记信息
        contentChanged = false;
        savedOrUpdated = true;
        // 保存完毕之后该数据实体就不是最新的了
        isNewModel = false;
    }

    protected void setResult() {
        // 没有更新过信息，直接返回
        if (!savedOrUpdated) {
//            super.onBackPressed();
        }

        // 信息更新过，根据需要决定是否将更新的信息返回到上一层
        if (getArguments().containsKey(Constants.EXTRA_REQUEST_CODE)){
            Intent intent = new Intent();
            intent.putExtra(Constants.EXTRA_MODEL, getModel());
            if (getArguments().containsKey(Constants.EXTRA_POSITION)){
                intent.putExtra(Constants.EXTRA_POSITION,
                        getArguments().getInt(Constants.EXTRA_POSITION, 0));
            }
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        } else {
//            super.onBackPressed();
        }
    }

    protected void onBack(){
        if (isContentChanged()){
            new MaterialDialog.Builder(getContext())
                    .title(R.string.text_tips)
                    .content(R.string.text_save_or_discard)
                    .positiveText(R.string.text_save)
                    .negativeText(R.string.text_give_up)
                    .onPositive((materialDialog, dialogAction) -> {
                        if (!checkInputInfo()){
                            return;
                        }
                        // 更新或者插入数据
                        saveOrUpdateData();
                        // 返回信息给上一层
                        setResult();
                    })
//                    .onNegative((materialDialog, dialogAction) -> BaseModelFragment.super.onBackPressed())
                    .show();
        } else {
            // 如果不加这个，保存完了数据，再调用这个方法就不会把结果传回了
            setResult();
        }
    }



    /*右侧抽屉相关的方法*/
    protected void addShortcut(){
        if (isNewModel()) {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.text_tips)
//                    .content(R.string.text_save_and_retry_to_add_shortcut)
//                    .positiveText(R.string.text_save_and_retry)
                    .negativeText(R.string.text_give_up)
                    .onPositive((materialDialog, dialogAction) -> {
//                        // 保存数据
//                        if (!saveOrUpdateData()) return;
//                        // 添加快捷方式
//                        ShortcutHelper.addShortcut(getContext(), getModel());
//                        ToastUtils.makeToast(getContext(), R.string.successfully_add_shortcut);
                    }).show();
        } else {
//            ShortcutHelper.addShortcut(getActivity().getApplicationContext(), getModel());
//            ToastUtils.makeToast(getContext(), R.string.successfully_add_shortcut);
        }
    }

    protected void showColorPickerDialog(int titleRes) {
        if (!(getActivity() instanceof ContentActivity)) {
            throw new IllegalArgumentException("The associated activity must be content!");
        }
        new ColorChooserDialog.Builder((ContentActivity) getActivity(), titleRes)
                .preselect(primaryColor())
                .accentMode(false)
                .titleSub(titleRes)
//                .backButton(R.string.text_back)
                .doneButton(R.string.done_label)
                .cancelButton(R.string.text_cancel)
                .show();
    }

    /*标签相关*/
    protected void showTagEditDialog() {
//        SimpleEditDialog.newInstance("", tag -> {
//            if (TextUtils.isEmpty(tag)){
//                return;
//            }
//            if (tag.indexOf(';') != -1){
//                ToastUtils.makeToast(getContext(), R.string.illegal_label);
//                return;
//            }
//
//            // 获取标签
//            String tags = getTags();
//
//            tags = tags == null ? "" : tags;
//            tags = tags + tag + ";";
//            if (tags.length() > TextLength.LABELS_TOTAL_LENGTH.length) {
//                ToastUtils.makeToast(getContext(), R.string.total_labels_too_long);
//                return;
//            }
//
//            // 回调标签
//            onGetTags(tags);
//
//            addTagToLayout(tag);
//        }).setMaxLength(TextLength.LABEL_TEXT_LENGTH.length).show(getFragmentManager(), "SHOW_ADD_LABELS_DIALOG");
    }

    protected void showTagsEditDialog() {
//        SimpleEditDialog.newInstance(getTags() == null ? "" : getTags(),
//                content -> {
//                    content = content == null ? "" : content;
//                    if (!content.endsWith(";")){
//                        content = content + ";";
//                    }
//
//                    // 回调标签
//                    onGetTags(content);
//
//                    addTagsToLayout(content);
//                }).setMaxLength(TextLength.LABELS_TOTAL_LENGTH.length).show(getFragmentManager(), "SHOW_LABELS_LAYOUT");
    }

    protected FlowLayout getTagsLayout(){
        return null;
    }

    protected String getTags() {
        return "";
    }

    protected void onGetTags(String tags) {}

    protected void addTagsToLayout(String stringTags){
        if (getTagsLayout() == null) {
            return;
        }

        getTagsLayout().removeAllViews();
        if (stringTags == null){
            return;
        }
        String[] tags = stringTags.split(";");
        for (String tag : tags){
            addTagToLayout(tag);
        }
    }

    protected void addTagToLayout(String tag){
//        if (getTagsLayout() == null) {
//            return;
//        }
//
//        // 设置显示标签的控件
//        TextView tvLabel = new TextView(getContext());
//        int margin = ViewUtils.dp2Px(getContext(), 2f);
//        tvLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(tvLabel.getLayoutParams());
//        params.setMargins(margin, margin, margin, margin);
//        tvLabel.setLayoutParams(params);
//        tvLabel.setPadding(ViewUtils.dp2Px(getContext(), 5f), 0, DisplayUtils.dp2Px(getContext(), 5f), 0);
//        tvLabel.setBackgroundResource(R.drawable.label_background);
//        tvLabel.setText(tag);
//
//        // 将标签添加到布局中
//        getTagsLayout().addView(tvLabel);
    }
}