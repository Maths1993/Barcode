/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Dominique\\AndroidStudioProjects\\Barcode\\mobile\\src\\main\\aidl\\de\\dfki\\ccaal\\gestures\\IGestureRecognitionService.aidl
 */
package de.dfki.ccaal.gestures;
public interface IGestureRecognitionService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements de.dfki.ccaal.gestures.IGestureRecognitionService
{
private static final java.lang.String DESCRIPTOR = "de.dfki.ccaal.gestures.IGestureRecognitionService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an de.dfki.ccaal.gestures.IGestureRecognitionService interface,
 * generating a proxy if needed.
 */
public static de.dfki.ccaal.gestures.IGestureRecognitionService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof de.dfki.ccaal.gestures.IGestureRecognitionService))) {
return ((de.dfki.ccaal.gestures.IGestureRecognitionService)iin);
}
return new de.dfki.ccaal.gestures.IGestureRecognitionService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_startClassificationMode:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.startClassificationMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_stopClassificationMode:
{
data.enforceInterface(DESCRIPTOR);
this.stopClassificationMode();
reply.writeNoException();
return true;
}
case TRANSACTION_registerListener:
{
data.enforceInterface(DESCRIPTOR);
de.dfki.ccaal.gestures.IGestureRecognitionListener _arg0;
_arg0 = de.dfki.ccaal.gestures.IGestureRecognitionListener.Stub.asInterface(data.readStrongBinder());
this.registerListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterListener:
{
data.enforceInterface(DESCRIPTOR);
de.dfki.ccaal.gestures.IGestureRecognitionListener _arg0;
_arg0 = de.dfki.ccaal.gestures.IGestureRecognitionListener.Stub.asInterface(data.readStrongBinder());
this.unregisterListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_startLearnMode:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.startLearnMode(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_stopLearnMode:
{
data.enforceInterface(DESCRIPTOR);
this.stopLearnMode();
reply.writeNoException();
return true;
}
case TRANSACTION_onPushToGesture:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.onPushToGesture(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_deleteTrainingSet:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.deleteTrainingSet(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_deleteGesture:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.deleteGesture(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getGestureList:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.util.List<java.lang.String> _result = this.getGestureList(_arg0);
reply.writeNoException();
reply.writeStringList(_result);
return true;
}
case TRANSACTION_isLearning:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isLearning();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements de.dfki.ccaal.gestures.IGestureRecognitionService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void startClassificationMode(java.lang.String trainingSetName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(trainingSetName);
mRemote.transact(Stub.TRANSACTION_startClassificationMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopClassificationMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopClassificationMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void registerListener(de.dfki.ccaal.gestures.IGestureRecognitionListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterListener(de.dfki.ccaal.gestures.IGestureRecognitionListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void startLearnMode(java.lang.String trainingSetName, java.lang.String gestureName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(trainingSetName);
_data.writeString(gestureName);
mRemote.transact(Stub.TRANSACTION_startLearnMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopLearnMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopLearnMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onPushToGesture(boolean pushed) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((pushed)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_onPushToGesture, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void deleteTrainingSet(java.lang.String trainingSetName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(trainingSetName);
mRemote.transact(Stub.TRANSACTION_deleteTrainingSet, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void deleteGesture(java.lang.String trainingSetName, java.lang.String gestureName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(trainingSetName);
_data.writeString(gestureName);
mRemote.transact(Stub.TRANSACTION_deleteGesture, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.util.List<java.lang.String> getGestureList(java.lang.String trainingSet) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<java.lang.String> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(trainingSet);
mRemote.transact(Stub.TRANSACTION_getGestureList, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArrayList();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isLearning() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isLearning, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_startClassificationMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_stopClassificationMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_registerListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_unregisterListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_startLearnMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_stopLearnMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_onPushToGesture = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_deleteTrainingSet = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_deleteGesture = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getGestureList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_isLearning = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
}
public void startClassificationMode(java.lang.String trainingSetName) throws android.os.RemoteException;
public void stopClassificationMode() throws android.os.RemoteException;
public void registerListener(de.dfki.ccaal.gestures.IGestureRecognitionListener listener) throws android.os.RemoteException;
public void unregisterListener(de.dfki.ccaal.gestures.IGestureRecognitionListener listener) throws android.os.RemoteException;
public void startLearnMode(java.lang.String trainingSetName, java.lang.String gestureName) throws android.os.RemoteException;
public void stopLearnMode() throws android.os.RemoteException;
public void onPushToGesture(boolean pushed) throws android.os.RemoteException;
public void deleteTrainingSet(java.lang.String trainingSetName) throws android.os.RemoteException;
public void deleteGesture(java.lang.String trainingSetName, java.lang.String gestureName) throws android.os.RemoteException;
public java.util.List<java.lang.String> getGestureList(java.lang.String trainingSet) throws android.os.RemoteException;
public boolean isLearning() throws android.os.RemoteException;
}
