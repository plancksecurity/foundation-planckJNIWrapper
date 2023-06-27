package foundation.pEp.jniadapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class _Group {
    public _Identity group_identity;   //!< identity representing this group
    public _Identity manager;          //!< identity of the group manager
    public Vector<_Member> members;           //!< list of members associated with group
    public boolean active = false;                    //!< boolean true if group is marked as active, else false


    public _Group() {
        active = false;
    }

    public _Group(Group group) {
        this.group_identity = new _Identity(group.group_identity);
        this.manager = new _Identity(group.manager);
        this.members = new Vector<>();
        for (Member member : group.members) {
            members.add(new _Member(member));
        }
        this.active = group.active;

    }
}
