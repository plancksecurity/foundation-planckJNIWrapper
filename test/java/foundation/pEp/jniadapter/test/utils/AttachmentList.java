package foundation.pEp.jniadapter.test.utils;

import foundation.pEp.jniadapter.Blob;

import java.util.Vector;

public class AttachmentList {
    private Vector<Blob> attachments;
    private int count = 1;
    private int dataSize = 100;

    public Vector<Blob> getAttachments() {
        return attachments;
    }

    public int getCount() {
        return count;
    }

    public int getDataSize() {
        return dataSize;
    }

    public AttachmentList(int count, int dataSize) {
        this.count = count;
        this.dataSize = dataSize;
        attachments = AdapterTestUtils.makeNewTestBlobList(dataSize, "attachment.txt", "text/plain", count);
    }
}
