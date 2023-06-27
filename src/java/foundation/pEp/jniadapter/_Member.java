package foundation.pEp.jniadapter;

public class _Member {
    _Identity ident;
    boolean joined;

    public _Member() {

    }

    public _Member(Member member) {
        this.ident = new _Identity(member.ident);
        this.joined = member.joined;
    }
}
