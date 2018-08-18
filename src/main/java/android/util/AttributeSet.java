package android.util;

public abstract interface AttributeSet {
	public abstract int getAttributeCount();

	public abstract String getAttributeName(int paramInt);

	public abstract String getAttributeValue(int paramInt);

	public abstract String getPositionDescription();

	public abstract int getAttributeNameResource(int paramInt);

	public abstract int getAttributeListValue(int paramInt1, String[] paramArrayOfString, int paramInt2);

	public abstract boolean getAttributeBooleanValue(int paramInt, boolean paramBoolean);

	public abstract int getAttributeResourceValue(int paramInt1, int paramInt2);

	public abstract int getAttributeIntValue(int paramInt1, int paramInt2);

	public abstract int getAttributeUnsignedIntValue(int paramInt1, int paramInt2);

	public abstract float getAttributeFloatValue(int paramInt, float paramFloat);

	public abstract String getIdAttribute();

	public abstract String getClassAttribute();

	public abstract int getIdAttributeResourceValue(int paramInt);

	public abstract int getStyleAttribute();

	public abstract String getAttributeValue(String paramString1, String paramString2);

	public abstract int getAttributeListValue(String paramString1, String paramString2, String[] paramArrayOfString,
			int paramInt);

	public abstract boolean getAttributeBooleanValue(String paramString1, String paramString2, boolean paramBoolean);

	public abstract int getAttributeResourceValue(String paramString1, String paramString2, int paramInt);

	public abstract int getAttributeIntValue(String paramString1, String paramString2, int paramInt);

	public abstract int getAttributeUnsignedIntValue(String paramString1, String paramString2, int paramInt);

	public abstract float getAttributeFloatValue(String paramString1, String paramString2, float paramFloat);

	public abstract int getAttributeValueType(int paramInt);

	public abstract int getAttributeValueData(int paramInt);
}
