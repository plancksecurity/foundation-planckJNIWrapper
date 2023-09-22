package foundation.pEp.jniadapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Group {
	public Identity group_identity;   //!< identity representing this group
	public Identity manager;          //!< identity of the group manager
	public Vector<Member> members;           //!< list of members associated with group
	public boolean active;                    //!< boolean true if group is marked as active, else false

	public Group() {

	}

	public Group(_Group group) {
		group_identity = new Identity(group.group_identity);
		manager = new Identity(group.manager);
		members = new Vector<>();
		for (_Member member : group.members) {
			members.add(new Member(member));
		}
		active = group.active;
	}
}
