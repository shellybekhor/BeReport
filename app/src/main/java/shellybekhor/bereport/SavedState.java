// not used at the moment

package shellybekhor.bereport;

import java.util.Map;

public class SavedState {

    private Map savedDateToHours;
    private Map savedMonthToHours;

    public void setDateToHours(Map dateToHours)
    {
        this.savedDateToHours = dateToHours;
    }

    public void setMonthToHours(Map monthToHours)
    {
        this.savedMonthToHours = monthToHours;
    }

    public Map getDateToHours(Map dateToHours)
    {
        return this.savedDateToHours;
    }

    public Map getMonthToHours(Map dateToHours)
    {
        return this.savedMonthToHours;
    }
}
