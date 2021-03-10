package foundation.pEp.jniadapter.test.jni147;


import foundation.pEp.jniadapter.test.utils.CTXBase;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;
import static foundation.pEp.jniadapter.Utils.*;


/*
JNI-147 - Utils: add a method to check two URIs for equlity

Definition:
On both URI's:
* remove leading and trailing withespace
* remove substring before and with "://"
compare the resulting strings byte by byte for case sensitive equality.

e.g.
"file://testname" == "testname"
*/


class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);
        CTXBase jni147Ctx = new CTXBase();

        new TestUnit<CTXBase>("URIEquals tests", new CTXBase(), ctx -> {
            // SAME
            assert URIEqual("", "") == true : "1";
            assert URIEqual("file://", "") == true : "2";
            assert URIEqual("", "file://") == true : "3";
            assert URIEqual("alice@peptest.com", "alice@peptest.com") == true : "4";
            assert URIEqual("mailto://alice@peptest.com", "alice@peptest.com") == true : "5";
            assert URIEqual("alice@peptest.com", "mailto://alice@peptest.com") == true : "6";
            assert URIEqual("mailto://alice@peptest.com", "mailto://alice@peptest.com") == true : "7";
            assert URIEqual("", " ") == true : "8";
            assert URIEqual("file://", " ") == true : "9";
            assert URIEqual(" ", "file://") == true : "10";
            // NOT SAME
            assert URIEqual("alice@peptest.com", "bob@peptest.com") == false : "11";
            assert URIEqual("mailto://alice@peptest.com", "bob@peptest.com") == false : "12";
            assert URIEqual("alice@peptest.com", "mailto://bob@peptest.com") == false : "13";
        });

        TestSuite.getDefault().run();
    }
}


