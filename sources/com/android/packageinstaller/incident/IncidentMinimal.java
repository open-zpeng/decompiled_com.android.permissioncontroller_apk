package com.android.packageinstaller.incident;

import com.android.packageinstaller.incident.RestrictedImagesDumpProto;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes.dex */
public final class IncidentMinimal extends GeneratedMessageLite<IncidentMinimal, Builder> implements IncidentMinimalOrBuilder {
    private static final IncidentMinimal DEFAULT_INSTANCE = new IncidentMinimal();
    private static volatile Parser<IncidentMinimal> PARSER;
    private int bitField0_;
    private Internal.ProtobufList<IncidentHeaderProto> header_ = GeneratedMessageLite.emptyProtobufList();
    private RestrictedImagesDumpProto restrictedImagesSection_;

    private IncidentMinimal() {
    }

    public int getHeaderCount() {
        return this.header_.size();
    }

    public IncidentHeaderProto getHeader(int i) {
        return this.header_.get(i);
    }

    public boolean hasRestrictedImagesSection() {
        return (this.bitField0_ & 1) == 1;
    }

    public RestrictedImagesDumpProto getRestrictedImagesSection() {
        RestrictedImagesDumpProto restrictedImagesDumpProto = this.restrictedImagesSection_;
        return restrictedImagesDumpProto == null ? RestrictedImagesDumpProto.getDefaultInstance() : restrictedImagesDumpProto;
    }

    public static IncidentMinimal parseFrom(InputStream inputStream) throws IOException {
        return (IncidentMinimal) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
    }

    /* loaded from: classes.dex */
    public static final class Builder extends GeneratedMessageLite.Builder<IncidentMinimal, Builder> implements IncidentMinimalOrBuilder {
        /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
            this();
        }

        private Builder() {
            super(IncidentMinimal.DEFAULT_INSTANCE);
        }
    }

    /* renamed from: com.android.packageinstaller.incident.IncidentMinimal$1  reason: invalid class name */
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
                return new IncidentMinimal();
            case 2:
                return DEFAULT_INSTANCE;
            case 3:
                this.header_.makeImmutable();
                return null;
            case 4:
                return new Builder(null);
            case 5:
                GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                IncidentMinimal incidentMinimal = (IncidentMinimal) obj2;
                this.header_ = visitor.visitList(this.header_, incidentMinimal.header_);
                this.restrictedImagesSection_ = (RestrictedImagesDumpProto) visitor.visitMessage(this.restrictedImagesSection_, incidentMinimal.restrictedImagesSection_);
                if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                    this.bitField0_ |= incidentMinimal.bitField0_;
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
                                if (!this.header_.isModifiable()) {
                                    this.header_ = GeneratedMessageLite.mutableCopy(this.header_);
                                }
                                this.header_.add((IncidentHeaderProto) codedInputStream.readMessage(IncidentHeaderProto.parser(), extensionRegistryLite));
                            } else if (readTag != 24202) {
                                if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            } else {
                                RestrictedImagesDumpProto.Builder builder = (this.bitField0_ & 1) == 1 ? this.restrictedImagesSection_.toBuilder() : null;
                                this.restrictedImagesSection_ = (RestrictedImagesDumpProto) codedInputStream.readMessage(RestrictedImagesDumpProto.parser(), extensionRegistryLite);
                                if (builder != null) {
                                    builder.mergeFrom((RestrictedImagesDumpProto.Builder) this.restrictedImagesSection_);
                                    this.restrictedImagesSection_ = builder.buildPartial();
                                }
                                this.bitField0_ |= 1;
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
                    synchronized (IncidentMinimal.class) {
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
}
