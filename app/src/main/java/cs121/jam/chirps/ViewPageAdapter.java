package cs121.jam.chirps;

/**
 * Created by Alexander on 11/4/2014.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class MyChirpsViewPagerAdapter extends FragmentPagerAdapter {

    // Declare the number of ViewPager pages
    final int PAGE_COUNT = 2;
    private String titles[] = new String[] { "Unaccepted Chirps", "Accepted Chirps" };

    public MyChirpsViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private Fragment[] fragments = new Fragment[2];

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            // Open FragmentTab1.java
            case 0:
                if(fragments[0] == null)
                    fragments[0] = ChirpFragment.newInstance(ChirpFragment.USER_CHIRP_QUERY, "FALSE");
                return fragments[0];

            // Open FragmentTab2.java
            case 1:
                if(fragments[1] == null)
                    fragments[1] = ChirpFragment.newInstance(ChirpFragment.USER_CHIRP_QUERY, "TRUE");
                return fragments[1];
        }
        return null;
    }

    public void refreshFragments() {
        for(Fragment frag : fragments) {
            ((ChirpFragment) frag).refreshList();
        }
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}

class UserProfileViewPagerAdapter extends FragmentPagerAdapter {

    // Declare the number of ViewPager pages
    final int PAGE_COUNT = 2;
    private String titles[] = new String[] { "User Information", "Posted Chirps" };
    private String userId;
    public UserProfileViewPagerAdapter(FragmentManager fm, String userId) {
        super(fm);
        this.userId = userId;
    }

    private Fragment[] fragments = new Fragment[2];

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            // Open FragmentTab1.java
            case 0:
                if(fragments[0] == null)
                    fragments[0] = UserProfileFragment.newInstance(userId, "FALSE");
                return fragments[0];

            // Open FragmentTab2.java
            case 1:
                if(fragments[1] == null)
                    fragments[1] = ChirpFragment.newInstance(ChirpFragment.GENERAL_USER_CHIRP_QUERY, userId);
                return fragments[1];
        }
        return null;
    }

    public void refreshFragments() {
        for(Fragment frag : fragments) {
            ((ChirpFragment) frag).refreshList();
        }
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}
