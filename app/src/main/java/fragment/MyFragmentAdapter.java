package fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by tobo on 17/2/3.
 */

public class MyFragmentAdapter extends FragmentStatePagerAdapter {
    public FragmentManager mFm;
    public MyFragmentAdapter(FragmentManager fm) {
        super(fm);
        mFm=fm;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
       switch (position){
           case 0:
               fragment=new ConversationFragment();
               break;
           case 1:
              fragment=new ContactFragment();

               break;
       }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
