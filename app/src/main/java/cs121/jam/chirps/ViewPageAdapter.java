package cs121.jam.chirps;

/**
 * Created by Alexander on 11/4/2014.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class ViewPagerAdapter extends FragmentPagerAdapter {

    // Declare the number of ViewPager pages
    final int PAGE_COUNT = 2;
    private String titles[] = new String[] { "Unaccepted Chirps", "AcceptedChirps" };

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            // Open FragmentTab1.java
            case 0:
                return ChirpFragment.newInstance(ChirpFragment.USER_CHIRP_QUERY, "FALSE");

            // Open FragmentTab2.java
            case 1:
                return ChirpFragment.newInstance(ChirpFragment.USER_CHIRP_QUERY, "TRUE");
        }
        return null;
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}
