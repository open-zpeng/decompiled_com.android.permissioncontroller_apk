package com.android.packageinstaller.incident;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import java.io.IOException;
/* loaded from: classes.dex */
public final class RestrictedImageProto extends GeneratedMessageLite<RestrictedImageProto, Builder> implements RestrictedImageProtoOrBuilder {
    private static final RestrictedImageProto DEFAULT_INSTANCE = new RestrictedImageProto();
    private static volatile Parser<RestrictedImageProto> PARSER;
    private int bitField0_;
    private ByteString imageData_;
    private ByteString metadata_;
    private String mimeType_ = "";

    private RestrictedImageProto() {
        ByteString byteString = ByteString.EMPTY;
        this.imageData_ = byteString;
        this.metadata_ = byteString;
    }

    public boolean hasMimeType() {
        return (this.bitField0_ & 1) == 1;
    }

    public String getMimeType() {
        return this.mimeType_;
    }

    public boolean hasImageData() {
        return (this.bitField0_ & 2) == 2;
    }

    public ByteString getImageData() {
        return this.imageData_;
    }

    public boolean hasMetadata() {
        return (this.bitField0_ & 4) == 4;
    }

    /* loaded from: classes.dex */
    public static final class Builder extends GeneratedMessageLite.Builder<RestrictedImageProto, Builder> implements RestrictedImageProtoOrBuilder {
        /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
            this();
        }

        private Builder() {
            super(RestrictedImageProto.DEFAULT_INSTANCE);
        }
    }

    /* renamed from: com.android.packageinstaller.incident.RestrictedImageProto$1  reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke = new int[GeneratedMessageLite.MethodToInvoke.values().length];

        static {
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.IS_INITIALIZED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.MAKE_IMMUTABLE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.NEW_BUILDER.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.VISIT.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.MERGE_FROM_STREAM.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.GET_DEFAULT_INSTANCE.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.GET_PARSER.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.google.protobuf.GeneratedMessageLite
    protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new RestrictedImageProto();
            case 2:
                return DEFAULT_INSTANCE;
            case 3:
                return null;
            case 4:
                return new Builder(null);
            case 5:
                GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                RestrictedImageProto restrictedImageProto = (RestrictedImageProto) obj2;
                this.mimeType_ = visitor.visitString(hasMimeType(), this.mimeType_, restrictedImageProto.hasMimeType(), restrictedImageProto.mimeType_);
                this.imageData_ = visitor.visitByteString(hasImageData(), this.imageData_, restrictedImageProto.hasImageData(), restrictedImageProto.imageData_);
                this.metadata_ = visitor.visitByteString(hasMetadata(), this.metadata_, restrictedImageProto.hasMetadata(), restrictedImageProto.metadata_);
                if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                    this.bitField0_ |= restrictedImageProto.bitField0_;
                }
                return this;
            case 6:
                CodedInputStream codedInputStream = (CodedInputStream) obj;
                ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                boolean z = false;
                while (!z) {
                    try {
                        int readTag = codedInputStream.readTag();
                        if (readTag != 0) {
                            if (readTag == 10) {
                                String readString = codedInputStream.readString();
                                this.bitField0_ = 1 | this.bitField0_;
                                this.mimeType_ = readString;
                            } else if (readTag == 18) {
                                this.bitField0_ |= 2;
                                this.imageData_ = codedInputStream.readBytes();
                            } else if (readTag != 26) {
                                if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            } else {
                                this.bitField0_ |= 4;
                                this.metadata_ = codedInputStream.readBytes();
                            }
                        }
                        z = true;
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException(e.setUnfinishedMessage(this));
                    } catch (IOException e2) {
                        throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                    }
                }
                break;
            case 7:
                break;
            case 8:
                if (PARSER == null) {
                    synchronized (RestrictedImageProto.class) {
                        if (PARSER == null) {
                            PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                        }
                    }
                }
                return PARSER;
            default:
                throw new UnsupportedOperationException();
        }
        return DEFAULT_INSTANCE;
    }

    static {
        DEFAULT_INSTANCE.makeImmutable();
    }

    public static Parser<RestrictedImageProto> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
