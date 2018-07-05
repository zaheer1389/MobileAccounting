package com.adslinfotech.mobileaccounting.adapter.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.adapter.ParentAdapter;
import com.adslinfotech.mobileaccounting.dao.NoteDao;
import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter
  extends ParentAdapter
{
  private Context mContext;
  private final LayoutInflater mInflater;
  
  public NoteListAdapter(Context paramContext, ArrayList<NoteDao> paramArrayList)
  {
    this.mContext = paramContext;
    this.mInflater = LayoutInflater.from(paramContext);
    this.mFileList = paramArrayList;
    this.mFilteredFile = paramArrayList;
  }
  
  public int getCount()
  {
    return this.mFilteredFile.size();
  }
  
  public List<NoteDao> getFilteredResults()
  {
    return this.mFilteredFile;
  }
  
  public List<NoteDao> getFilteredResults(CharSequence paramCharSequence)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramCharSequence != null)
    {
      int j = this.mFileList.size();
      int i = 0;
      while (i < j)
      {
        NoteDao localNoteDao = (NoteDao)this.mFileList.get(i);
        if ((localNoteDao.getHeading().toLowerCase().contains(paramCharSequence.toString().toLowerCase())) || (localNoteDao.getDescr().toLowerCase().contains(paramCharSequence.toString().toLowerCase()))) {
          localArrayList.add(localArrayList.size(), localNoteDao);
        }
        i += 1;
      }
    }
    return localArrayList;
  }
  
  public Object getItem(int paramInt)
  {
    return this.mFilteredFile.get(paramInt);
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public List<NoteDao> getList()
  {
    return this.mFileList;
  }
  
  public View getView(int paramInt, ViewHolder paramView, ViewGroup paramViewGroup)
  {
    View localView = null;
    if (paramView == null)
    {
      localView = this.mInflater.inflate(R.layout.list_row_account, paramViewGroup, false);
      paramView = new ViewHolder();
      localView.findViewById(R.id.img_acc).setVisibility(View.GONE);
      localView.findViewById(R.id.tv_acc_category).setVisibility(View.GONE);
      paramView.tvSubject = ((TextView)localView.findViewById(R.id.tv_rmd_sub));
      paramView.tvDesc = ((TextView)localView.findViewById(R.id.tv_rmd_desc));
      localView.setTag(paramView);
    }
    for (;;)
    {
      paramView.setData(paramInt);
      //paramView = AnimationUtils.loadAnimation(this.mContext, R.anim.fade_in);
      //paramView.setDuration(500L);
      //localView.startAnimation(paramView);
      paramView = (ViewHolder)localView.getTag();
      return localView;

    }
  }
  
  private class ViewHolder
  {
    TextView tvDesc;
    TextView tvSubject;
    
    private ViewHolder() {}
    
    public void setData(int paramInt)
    {
      NoteDao localNoteDao = (NoteDao)NoteListAdapter.this.mFilteredFile.get(paramInt);
      this.tvSubject.setText(localNoteDao.getHeading());
      this.tvDesc.setText(localNoteDao.getDescr());
    }
  }
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/com/adslinfotech/mobileaccounting/adapter/home/NoteListAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */