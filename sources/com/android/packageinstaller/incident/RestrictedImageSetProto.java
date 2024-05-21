package com.android.packageinstaller.incident;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import java.io.IOException;
/* loaded from: classes.dex */
public final class RestrictedImageSetProto extends GeneratedMessageLite<RestrictedImageSetProto, Builder> implements RestrictedImageSetProtoOrBuilder {
    private static final RestrictedImageSetProto DEFAULT_INSTANCE = new RestrictedImageSetProto();
    private static volatile Parser<RestrictedImageSetProto> PARSER;
    private int bitField0_;
    private String category_ = "";
    private Internal.ProtobufList<RestrictedImageProto> images_ = GeneratedMessageLite.emptyProtobufList();
    private ByteString metadata_ = ByteString.EMPTY;

    private RestrictedImageSetProto() {
    }

    public boolean hasCategory() {
        return (this.bitField0_ & 1) == 1;
    }

    public int getImagesCount() {
        return this.images_.size();
    }

    public RestrictedImageProto getImages(int i) {
        return this.images_.get(i);
    }

    public boolean hasMetadata() {
        return (this.bitField0_ & 2) == 2;
    }

    /* loaded from: classes.dex */
    public static final class Builder extends GeneratedMessageLite.Builder<RestrictedImageSetProto, Builder> implements RestrictedImageSetProtoOrBuilder {
        /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
            this();
        }

        private Builder() {
            super(RestrictedImageSetProto.DEFAULT_INSTANCE);
        }
    }

    /* renamed from: com.android.packageinstaller.incident.RestrictedImageSetProto$1  reason: invalid class name */
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
                return new RestrictedImageSetProto();
            case 2:
                return DEFAULT_INSTANCE;
            case 3:
                this.images_.makeImmutable();
                return null;
            case 4:
                return new Builder(null);
            case 5:
                GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                RestrictedImageSetProto restrictedImageSetProto = (RestrictedImageSetProto) obj2;
                this.category_ = visitor.visitString(hasCategory(), this.category_, restrictedImageSetProto.hasCategory(), restrictedImageSetProto.category_);
                this.images_ = visitor.visitList(this.images_, restrictedImageSetProto.images_);
                this.metadata_ = visitor.visitByteString(hasMetadata(), this.metadata_, restrictedImageSetProto.hasMetadata(), restrictedImageSetProto.metadata_);
                if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                    this.bitField0_ |= restrictedImageSetProto.bitField0_;
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
                                this.category_ = readString;
                            } else if (readTag == 18) {
                                if (!this.images_.isModifiable()) {
                                    this.images_ = GeneratedMessageLite.mutableCopy(this.images_);
                                }
                                this.images_.add((RestrictedImageProto) codedInputStream.readMessage(RestrictedImageProto.parser(), extensionRegistryLite));
                            } else if (readTag != 26) {
                                if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            } else {
                                this.bitField0_ |= 2;
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
                    synchronized (RestrictedImageSetProto.class) {
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

    public static Parser<RestrictedImageSetProto> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
