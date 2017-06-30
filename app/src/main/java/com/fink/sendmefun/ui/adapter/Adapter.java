package com.fink.sendmefun.ui.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fink.sendmefun.R;
import com.fink.sendmefun.model.message.Message;
import com.fink.sendmefun.model.message.ReceivedMessage;
import com.fink.sendmefun.model.message.SentMessage;

import java.util.List;

public class Adapter extends ArrayAdapter<Message> {

    private int resource;

    public Adapter(Context context,int resource, List<Message> message) {
        super(context,resource,message);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message item = getItem(position);
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.image = (ImageView) convertView.findViewById(R.id.imageStatus);
            convertView.setTag(holder);
        }

        holder = holder == null ? (ViewHolder) convertView.getTag() : holder;


        holder.message.setText(item.getText());
        if(item.getType() == ReceivedMessage.TYPE){
            holder.message.setBackgroundResource(R.drawable.text_view_in_style);
        }else{
            holder.message.setBackgroundResource(R.drawable.text_view_out_style);
            int status = ((SentMessage)item).getStatus();
            if(status != SentMessage.NO_IMAGE) {
                switch (status) {
                    case SentMessage.PROGRESS:
                        holder.image.setImageResource(R.drawable.progress);
                        break;
                    case SentMessage.CHECK_MARK:
                        holder.image.setImageResource(R.drawable.check_mark);
                        break;
                    case SentMessage.CROSS:
                        holder.image.setImageResource(R.drawable.cross);
                        break;
                }
                int check_mark_margin = (int) convertView.getResources().getDimension(R.dimen.check_mark_margin);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(holder.image.getLayoutParams());
                lp.setMargins(check_mark_margin, check_mark_margin, check_mark_margin, check_mark_margin);
                lp.gravity = Gravity.BOTTOM;
                holder.image.setLayoutParams(lp);
            }
        }

        return convertView;

    }

    private class ViewHolder {
        private TextView message;
        private ImageView image;
    }
}
