package foundation.pEp.jniadapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class _Group {
    _Identity group_identity;   //!< identity representing this group
    _Identity manager;          //!< identity of the group manager
    Vector<_Member> members;           //!< list of members associated with group
    boolean active;                    //!< boolean true if group is marked as active, else false


    public _Group() {

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
