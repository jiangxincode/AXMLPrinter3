package android.content.res;

import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;

public abstract interface XmlResourceParser extends XmlPullParser, AttributeSet {
	public abstract void close();
}
