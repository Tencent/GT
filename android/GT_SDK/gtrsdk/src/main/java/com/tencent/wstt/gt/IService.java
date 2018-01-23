/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\p_hongjcong.TENCENT\\Desktop\\GT-master\\android\\sdk\\src\\com\\tencent\\wstt\\gt\\IService.aidl
 */
package com.tencent.wstt.gt;

public interface IService extends android.os.IInterface {
    /** Local-side IPC implementation stub class. */
    abstract class Stub extends android.os.Binder implements IService {
        private static final String DESCRIPTOR = "com.tencent.wstt.gt.IService";

        /** Construct the stub at attach it to the interface. */
        public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}

        /**
         * Cast an IBinder object into an com.tencent.wstt.gt.IService interface,
         * generating a proxy if needed.
         */
        public static IService asInterface(android.os.IBinder obj) {
            if (obj == null) {
                return null;
            }

            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && iin instanceof IService) {
                return (IService)iin;
            }
            return new Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder()
{
return this;
}

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_checkIsCanConnect: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    int _arg1;
                    _arg1 = data.readInt();
                    int _result = this.checkIsCanConnect(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_initConnectGT: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    int _arg1;
                    _arg1 = data.readInt();
                    this.initConnectGT(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_disconnectGT: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    boolean _result = this.disconnectGT(_arg0);
                    reply.writeNoException();
                    reply.writeInt(((_result)?(1):(0)));
                    return true;
                }
                case TRANSACTION_registerOutPara: {
                    data.enforceInterface(DESCRIPTOR);
                    OutPara _arg0;
                    if ((0!=data.readInt())) {
                        _arg0 = OutPara.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    this.registerOutPara(_arg0);
                    return true;
                }
                case TRANSACTION_registerGlobalOutPara: {
                    data.enforceInterface(DESCRIPTOR);
                    OutPara _arg0;
                    if ((0!=data.readInt())) {
                        _arg0 = OutPara.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    this.registerGlobalOutPara(_arg0);
                    return true;
                }
                case TRANSACTION_getOutPara: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    String _result = this.getOutPara(_arg0);
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                }
                case TRANSACTION_getGlobalOutPara: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    String _result = this.getGlobalOutPara(_arg0);
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                }
                case TRANSACTION_setOutPara: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    String _arg1;
                    _arg1 = data.readString();
                    this.setOutPara(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setGlobalOutPara: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    String _arg1;
                    _arg1 = data.readString();
                    this.setGlobalOutPara(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setTimedOutPara: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    long _arg1;
                    _arg1 = data.readLong();
                    String _arg2;
                    _arg2 = data.readString();
                    this.setTimedOutPara(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_registerInPara: {
                    data.enforceInterface(DESCRIPTOR);
                    com.tencent.wstt.gt.InPara _arg0;
                    if ((0!=data.readInt())) {
                        _arg0 = com.tencent.wstt.gt.InPara.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    this.registerInPara(_arg0);
                    return true;
                }
                case TRANSACTION_registerGlobalInPara: {
                    data.enforceInterface(DESCRIPTOR);
                    com.tencent.wstt.gt.InPara _arg0;
                    if ((0!=data.readInt())) {
                        _arg0 = com.tencent.wstt.gt.InPara.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    this.registerGlobalInPara(_arg0);
                    return true;
                }
                case TRANSACTION_getInPara: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    com.tencent.wstt.gt.InPara _result = this.getInPara(_arg0);
                    reply.writeNoException();
                    if ((_result!=null)) {
                    reply.writeInt(1);
                        _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                }
                case TRANSACTION_getGlobalInPara: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    com.tencent.wstt.gt.InPara _result = this.getGlobalInPara(_arg0);
                    reply.writeNoException();
                    if ((_result!=null)) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);}
                    else {
                        reply.writeInt(0);
                    }
                    return true;
                }
                case TRANSACTION_setInPara: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    String _arg1;
                    _arg1 = data.readString();
                    this.setInPara(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setGlobalInPara: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    String _arg1;
                    _arg1 = data.readString();
                    this.setGlobalInPara(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_log: {
                    data.enforceInterface(DESCRIPTOR);
                    long _arg0;
                    _arg0 = data.readLong();
                    int _arg1;
                    _arg1 = data.readInt();
                    String _arg2;
                    _arg2 = data.readString();
                    String _arg3;
                    _arg3 = data.readString();
                    this.log(_arg0, _arg1, _arg2, _arg3);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setPerfDigitalEntry: {
                    data.enforceInterface(DESCRIPTOR);
                    com.tencent.wstt.gt.PerfDigitalEntry _arg0;
                    if ((0!=data.readInt())) {
                        _arg0 = com.tencent.wstt.gt.PerfDigitalEntry.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    this.setPerfDigitalEntry(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setPerfStringEntry: {
                    data.enforceInterface(DESCRIPTOR);
                    PerfStringEntry _arg0;
                    if ((0!=data.readInt())) {
                        _arg0 = PerfStringEntry.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    this.setPerfStringEntry(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setBooleanEntry: {
                    data.enforceInterface(DESCRIPTOR);
                    BooleanEntry _arg0;
                    if ((0!=data.readInt())) {
                        _arg0 = BooleanEntry.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    this.setBooleanEntry(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setCommond: {
                    data.enforceInterface(DESCRIPTOR);
                    android.os.Bundle _arg0;
                    if ((0!=data.readInt())) {
                        _arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    this.setCommond(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setCommondSync: {
                    data.enforceInterface(DESCRIPTOR);
                    android.os.Bundle _arg0;
                    if ((0!=data.readInt())) {
                        _arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    this.setCommondSync(_arg0);
                    reply.writeNoException();
                    if ((_arg0!=null)) {
                        reply.writeInt(1);
                        _arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IService {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote)
{
mRemote = remote;
}

            @Override
            public android.os.IBinder asBinder()
{
return mRemote;
}

            public String getInterfaceDescriptor()
{
return DESCRIPTOR;
}

            @Override
            public int checkIsCanConnect(String cur_pkgName, int versionId) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(cur_pkgName);
                    _data.writeInt(versionId);
                    mRemote.transact(Stub.TRANSACTION_checkIsCanConnect, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void initConnectGT(String pkgName, int pid) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(pkgName);
                    _data.writeInt(pid);
                    mRemote.transact(Stub.TRANSACTION_initConnectGT, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public boolean disconnectGT(String cur_pkgName) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                boolean _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(cur_pkgName);
                    mRemote.transact(Stub.TRANSACTION_disconnectGT, _data, _reply, 0);
                    _reply.readException();
                    _result = (0!=_reply.readInt());
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void registerOutPara(OutPara outPara) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((outPara!=null)) {
                    _data.writeInt(1);
                    outPara.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_registerOutPara, _data, null, android.os.IBinder.FLAG_ONEWAY);
                } finally {
                    _data.recycle();
                }
            }

            @Override
            public void registerGlobalOutPara(OutPara outPara) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((outPara!=null)) {
                        _data.writeInt(1);
                        outPara.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_registerGlobalOutPara, _data, null, android.os.IBinder.FLAG_ONEWAY);
                } finally {
                    _data.recycle();
                }
             }

            @Override
            public String getOutPara(String key) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(key);
                    mRemote.transact(Stub.TRANSACTION_getOutPara, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public String getGlobalOutPara(String key) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(key);
                    mRemote.transact(Stub.TRANSACTION_getGlobalOutPara, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void setOutPara(String key, String value) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeString(value);
                    mRemote.transact(Stub.TRANSACTION_setOutPara, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setGlobalOutPara(String key, String value) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeString(value);
                    mRemote.transact(Stub.TRANSACTION_setGlobalOutPara, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setTimedOutPara(String key, long time, String value) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeLong(time);
                    _data.writeString(value);
                    mRemote.transact(Stub.TRANSACTION_setTimedOutPara, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void registerInPara(com.tencent.wstt.gt.InPara inPara) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((inPara != null)) {
                        _data.writeInt(1);
                        inPara.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_registerInPara, _data, null, android.os.IBinder.FLAG_ONEWAY);
                } finally {
                    _data.recycle();
                }
            }

            @Override
            public void registerGlobalInPara(com.tencent.wstt.gt.InPara inPara) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((inPara != null)) {
                        _data.writeInt(1);
                        inPara.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_registerGlobalInPara, _data, null, android.os.IBinder.FLAG_ONEWAY);
                } finally {
                    _data.recycle();
                }
            }

            @Override
            public com.tencent.wstt.gt.InPara getInPara(String key) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                com.tencent.wstt.gt.InPara _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(key);
                    mRemote.transact(Stub.TRANSACTION_getInPara, _data, _reply, 0);
                    _reply.readException();
                    if ((0 != _reply.readInt())) {
                        _result = com.tencent.wstt.gt.InPara.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public com.tencent.wstt.gt.InPara getGlobalInPara(String key) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                com.tencent.wstt.gt.InPara _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(key);
                    mRemote.transact(Stub.TRANSACTION_getGlobalInPara, _data, _reply, 0);
                    _reply.readException();
                    if ((0 != _reply.readInt())) {
                        _result = com.tencent.wstt.gt.InPara.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void setInPara(String key, String value) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeString(value);
                    mRemote.transact(Stub.TRANSACTION_setInPara, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setGlobalInPara(String key, String value) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeString(value);
                    mRemote.transact(Stub.TRANSACTION_setGlobalInPara, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void log(long tid, int level, String tag, String msg) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(tid);
                    _data.writeInt(level);
                    _data.writeString(tag);
                    _data.writeString(msg);
                    mRemote.transact(Stub.TRANSACTION_log, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setPerfDigitalEntry(com.tencent.wstt.gt.PerfDigitalEntry task) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((task != null)) {
                        _data.writeInt(1);
                        task.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_setPerfDigitalEntry, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setPerfStringEntry(PerfStringEntry task) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((task != null)) {
                        _data.writeInt(1);
                        task.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_setPerfStringEntry, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setBooleanEntry(BooleanEntry task) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((task != null)) {
                        _data.writeInt(1);
                        task.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_setBooleanEntry, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setCommond(android.os.Bundle bundle) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((bundle != null)) {
                        _data.writeInt(1);
                        bundle.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_setCommond, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setCommondSync(android.os.Bundle bundle) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((bundle != null)) {
                        _data.writeInt(1);
                        bundle.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_setCommondSync, _data, _reply, 0);
                    _reply.readException();
                    if ((0 != _reply.readInt())) {
                        bundle.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_checkIsCanConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_initConnectGT = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_disconnectGT = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
        static final int TRANSACTION_registerOutPara = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
        static final int TRANSACTION_registerGlobalOutPara = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
        static final int TRANSACTION_getOutPara = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
        static final int TRANSACTION_getGlobalOutPara = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
        static final int TRANSACTION_setOutPara = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
        static final int TRANSACTION_setGlobalOutPara = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
        static final int TRANSACTION_setTimedOutPara = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
        static final int TRANSACTION_registerInPara = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
        static final int TRANSACTION_registerGlobalInPara = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
        static final int TRANSACTION_getInPara = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
        static final int TRANSACTION_getGlobalInPara = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
        static final int TRANSACTION_setInPara = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
        static final int TRANSACTION_setGlobalInPara = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
        static final int TRANSACTION_log = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
        static final int TRANSACTION_setPerfDigitalEntry = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
        static final int TRANSACTION_setPerfStringEntry = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
        static final int TRANSACTION_setBooleanEntry = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
        static final int TRANSACTION_setCommond = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
        static final int TRANSACTION_setCommondSync = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
    }

    int checkIsCanConnect(String cur_pkgName, int versionId) throws android.os.RemoteException;

    void initConnectGT(String pkgName, int pid) throws android.os.RemoteException;

    boolean disconnectGT(String cur_pkgName) throws android.os.RemoteException;

    void registerOutPara(OutPara outPara) throws android.os.RemoteException;

    void registerGlobalOutPara(OutPara outPara) throws android.os.RemoteException;

    String getOutPara(String key) throws android.os.RemoteException;

    String getGlobalOutPara(String key) throws android.os.RemoteException;

    void setOutPara(String key, String value) throws android.os.RemoteException;

    void setGlobalOutPara(String key, String value) throws android.os.RemoteException;

    void setTimedOutPara(String key, long time, String value) throws android.os.RemoteException;

    void registerInPara(com.tencent.wstt.gt.InPara inPara) throws android.os.RemoteException;

    void registerGlobalInPara(com.tencent.wstt.gt.InPara inPara) throws android.os.RemoteException;

    com.tencent.wstt.gt.InPara getInPara(String key) throws android.os.RemoteException;

    com.tencent.wstt.gt.InPara getGlobalInPara(String key) throws android.os.RemoteException;

    void setInPara(String key, String value) throws android.os.RemoteException;

    void setGlobalInPara(String key, String value) throws android.os.RemoteException;

    void log(long tid, int level, String tag, String msg) throws android.os.RemoteException;

    void setPerfDigitalEntry(com.tencent.wstt.gt.PerfDigitalEntry task) throws android.os.RemoteException;

    void setPerfStringEntry(PerfStringEntry task) throws android.os.RemoteException;

    void setBooleanEntry(BooleanEntry task) throws android.os.RemoteException;

    void setCommond(android.os.Bundle bundle) throws android.os.RemoteException;

    void setCommondSync(android.os.Bundle bundle) throws android.os.RemoteException;
}
