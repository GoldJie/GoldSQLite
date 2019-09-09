package com.lxj.goldsqlite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixinjie on 2019/9/9
 *
 */
public class ListViewAdapter extends BaseAdapter {
    private Context mContext;
    private List mDataList;

    public ListViewAdapter(@NotNull Context context) {
        this.mContext = context;
        this.mDataList = new ArrayList<>();
    }

    public ListViewAdapter(@NotNull Context context, @NotNull List dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_of_list, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
//        ViewHolder绑定数据
        if(viewHolder != null){
            onDataBinding(viewHolder, i);
        }
        return view;
    }

    /**
     * 绑定数据
     * @param viewHolder    viewholder
     * @param position      数据索引
     */
    private void onDataBinding(@NotNull ViewHolder viewHolder, int position){
        if(viewHolder.mTextTv != null){
            if(mDataList.get(position) != null){
                if(mDataList.get(position) instanceof Student){
                    handleStudentData(viewHolder.mTextTv, (Student) mDataList.get(position));
                }else if(mDataList.get(position) instanceof Teacher){
                    handleTeacherData(viewHolder.mTextTv, (Teacher) mDataList.get(position));
                }
            }
        }
    }

    /**
     * 处理学生数据
     */
    private void handleStudentData(@NotNull TextView textView, @NotNull Student student){
        String info =  "名称:" + student.getName() + "; 性别：" + student.getSex() +
                "; 班级：" + student.getClassz() + "; 成绩:" +student.getGrade() + "; 家庭：" + student.getFamily();
        textView.setText(info);
    }

    /**
     * 处理老师数据
     */
    private void handleTeacherData(@NotNull TextView textView, @NotNull Teacher teacher){
        String info = "名称:" + teacher.getName() + "; 性别：" + teacher.getSex() + "; 课程：" + teacher.getCourse();
        textView.setText(info);
    }

    /**
     * 设置数据源列表
     * @param dataList  数据源列表
     */
    public void setDataList(List dataList) {
        this.mDataList = dataList;
    }

    /**
     * 获取列表数据源
     */
    public List getDataList() {
        return mDataList;
    }

    /**
     * ViewHolder
     */
    class ViewHolder {
        TextView mTextTv;

        public ViewHolder(@NotNull View view) {
            this.mTextTv = view.findViewById(R.id.tv_text);
        }
    }
}
