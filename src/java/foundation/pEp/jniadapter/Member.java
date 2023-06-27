package foundation.pEp.jniadapter;

public class Member {
    public Identity ident;
    public boolean joined;

    public Member() {}

    public Member(_Member member) {
        ident = new Identity(member.ident);
        joined = member.joined;
    }
}
