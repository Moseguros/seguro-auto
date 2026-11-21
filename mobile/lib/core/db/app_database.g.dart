// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'app_database.dart';

// ignore_for_file: type=lint
class $InspectionUploadsTable extends InspectionUploads
    with TableInfo<$InspectionUploadsTable, InspectionUpload> {
  @override
  final GeneratedDatabase attachedDatabase;
  final String? _alias;
  $InspectionUploadsTable(this.attachedDatabase, [this._alias]);
  static const VerificationMeta _clientPhotoIdMeta = const VerificationMeta(
    'clientPhotoId',
  );
  @override
  late final GeneratedColumn<String> clientPhotoId = GeneratedColumn<String>(
    'client_photo_id',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _proposalIdMeta = const VerificationMeta(
    'proposalId',
  );
  @override
  late final GeneratedColumn<String> proposalId = GeneratedColumn<String>(
    'proposal_id',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _slotMeta = const VerificationMeta('slot');
  @override
  late final GeneratedColumn<String> slot = GeneratedColumn<String>(
    'slot',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _filePathMeta = const VerificationMeta(
    'filePath',
  );
  @override
  late final GeneratedColumn<String> filePath = GeneratedColumn<String>(
    'file_path',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _takenAtMeta = const VerificationMeta(
    'takenAt',
  );
  @override
  late final GeneratedColumn<DateTime> takenAt = GeneratedColumn<DateTime>(
    'taken_at',
    aliasedName,
    false,
    type: DriftSqlType.dateTime,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _latitudeMeta = const VerificationMeta(
    'latitude',
  );
  @override
  late final GeneratedColumn<double> latitude = GeneratedColumn<double>(
    'latitude',
    aliasedName,
    true,
    type: DriftSqlType.double,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _longitudeMeta = const VerificationMeta(
    'longitude',
  );
  @override
  late final GeneratedColumn<double> longitude = GeneratedColumn<double>(
    'longitude',
    aliasedName,
    true,
    type: DriftSqlType.double,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _statusMeta = const VerificationMeta('status');
  @override
  late final GeneratedColumn<String> status = GeneratedColumn<String>(
    'status',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _attemptsMeta = const VerificationMeta(
    'attempts',
  );
  @override
  late final GeneratedColumn<int> attempts = GeneratedColumn<int>(
    'attempts',
    aliasedName,
    false,
    type: DriftSqlType.int,
    requiredDuringInsert: false,
    defaultValue: const Constant(0),
  );
  static const VerificationMeta _nextAttemptAtMeta = const VerificationMeta(
    'nextAttemptAt',
  );
  @override
  late final GeneratedColumn<DateTime> nextAttemptAt =
      GeneratedColumn<DateTime>(
        'next_attempt_at',
        aliasedName,
        false,
        type: DriftSqlType.dateTime,
        requiredDuringInsert: true,
      );
  static const VerificationMeta _lastErrorCodeMeta = const VerificationMeta(
    'lastErrorCode',
  );
  @override
  late final GeneratedColumn<String> lastErrorCode = GeneratedColumn<String>(
    'last_error_code',
    aliasedName,
    true,
    type: DriftSqlType.string,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _remotePhotoIdMeta = const VerificationMeta(
    'remotePhotoId',
  );
  @override
  late final GeneratedColumn<String> remotePhotoId = GeneratedColumn<String>(
    'remote_photo_id',
    aliasedName,
    true,
    type: DriftSqlType.string,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _createdAtMeta = const VerificationMeta(
    'createdAt',
  );
  @override
  late final GeneratedColumn<DateTime> createdAt = GeneratedColumn<DateTime>(
    'created_at',
    aliasedName,
    false,
    type: DriftSqlType.dateTime,
    requiredDuringInsert: true,
  );
  @override
  List<GeneratedColumn> get $columns => [
    clientPhotoId,
    proposalId,
    slot,
    filePath,
    takenAt,
    latitude,
    longitude,
    status,
    attempts,
    nextAttemptAt,
    lastErrorCode,
    remotePhotoId,
    createdAt,
  ];
  @override
  String get aliasedName => _alias ?? actualTableName;
  @override
  String get actualTableName => $name;
  static const String $name = 'inspection_uploads';
  @override
  VerificationContext validateIntegrity(
    Insertable<InspectionUpload> instance, {
    bool isInserting = false,
  }) {
    final context = VerificationContext();
    final data = instance.toColumns(true);
    if (data.containsKey('client_photo_id')) {
      context.handle(
        _clientPhotoIdMeta,
        clientPhotoId.isAcceptableOrUnknown(
          data['client_photo_id']!,
          _clientPhotoIdMeta,
        ),
      );
    } else if (isInserting) {
      context.missing(_clientPhotoIdMeta);
    }
    if (data.containsKey('proposal_id')) {
      context.handle(
        _proposalIdMeta,
        proposalId.isAcceptableOrUnknown(data['proposal_id']!, _proposalIdMeta),
      );
    } else if (isInserting) {
      context.missing(_proposalIdMeta);
    }
    if (data.containsKey('slot')) {
      context.handle(
        _slotMeta,
        slot.isAcceptableOrUnknown(data['slot']!, _slotMeta),
      );
    } else if (isInserting) {
      context.missing(_slotMeta);
    }
    if (data.containsKey('file_path')) {
      context.handle(
        _filePathMeta,
        filePath.isAcceptableOrUnknown(data['file_path']!, _filePathMeta),
      );
    } else if (isInserting) {
      context.missing(_filePathMeta);
    }
    if (data.containsKey('taken_at')) {
      context.handle(
        _takenAtMeta,
        takenAt.isAcceptableOrUnknown(data['taken_at']!, _takenAtMeta),
      );
    } else if (isInserting) {
      context.missing(_takenAtMeta);
    }
    if (data.containsKey('latitude')) {
      context.handle(
        _latitudeMeta,
        latitude.isAcceptableOrUnknown(data['latitude']!, _latitudeMeta),
      );
    }
    if (data.containsKey('longitude')) {
      context.handle(
        _longitudeMeta,
        longitude.isAcceptableOrUnknown(data['longitude']!, _longitudeMeta),
      );
    }
    if (data.containsKey('status')) {
      context.handle(
        _statusMeta,
        status.isAcceptableOrUnknown(data['status']!, _statusMeta),
      );
    } else if (isInserting) {
      context.missing(_statusMeta);
    }
    if (data.containsKey('attempts')) {
      context.handle(
        _attemptsMeta,
        attempts.isAcceptableOrUnknown(data['attempts']!, _attemptsMeta),
      );
    }
    if (data.containsKey('next_attempt_at')) {
      context.handle(
        _nextAttemptAtMeta,
        nextAttemptAt.isAcceptableOrUnknown(
          data['next_attempt_at']!,
          _nextAttemptAtMeta,
        ),
      );
    } else if (isInserting) {
      context.missing(_nextAttemptAtMeta);
    }
    if (data.containsKey('last_error_code')) {
      context.handle(
        _lastErrorCodeMeta,
        lastErrorCode.isAcceptableOrUnknown(
          data['last_error_code']!,
          _lastErrorCodeMeta,
        ),
      );
    }
    if (data.containsKey('remote_photo_id')) {
      context.handle(
        _remotePhotoIdMeta,
        remotePhotoId.isAcceptableOrUnknown(
          data['remote_photo_id']!,
          _remotePhotoIdMeta,
        ),
      );
    }
    if (data.containsKey('created_at')) {
      context.handle(
        _createdAtMeta,
        createdAt.isAcceptableOrUnknown(data['created_at']!, _createdAtMeta),
      );
    } else if (isInserting) {
      context.missing(_createdAtMeta);
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {clientPhotoId};
  @override
  InspectionUpload map(Map<String, dynamic> data, {String? tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : '';
    return InspectionUpload(
      clientPhotoId: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}client_photo_id'],
      )!,
      proposalId: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}proposal_id'],
      )!,
      slot: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}slot'],
      )!,
      filePath: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}file_path'],
      )!,
      takenAt: attachedDatabase.typeMapping.read(
        DriftSqlType.dateTime,
        data['${effectivePrefix}taken_at'],
      )!,
      latitude: attachedDatabase.typeMapping.read(
        DriftSqlType.double,
        data['${effectivePrefix}latitude'],
      ),
      longitude: attachedDatabase.typeMapping.read(
        DriftSqlType.double,
        data['${effectivePrefix}longitude'],
      ),
      status: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}status'],
      )!,
      attempts: attachedDatabase.typeMapping.read(
        DriftSqlType.int,
        data['${effectivePrefix}attempts'],
      )!,
      nextAttemptAt: attachedDatabase.typeMapping.read(
        DriftSqlType.dateTime,
        data['${effectivePrefix}next_attempt_at'],
      )!,
      lastErrorCode: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}last_error_code'],
      ),
      remotePhotoId: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}remote_photo_id'],
      ),
      createdAt: attachedDatabase.typeMapping.read(
        DriftSqlType.dateTime,
        data['${effectivePrefix}created_at'],
      )!,
    );
  }

  @override
  $InspectionUploadsTable createAlias(String alias) {
    return $InspectionUploadsTable(attachedDatabase, alias);
  }
}

class InspectionUpload extends DataClass
    implements Insertable<InspectionUpload> {
  final String clientPhotoId;
  final String proposalId;
  final String slot;
  final String filePath;
  final DateTime takenAt;
  final double? latitude;
  final double? longitude;
  final String status;
  final int attempts;
  final DateTime nextAttemptAt;
  final String? lastErrorCode;
  final String? remotePhotoId;
  final DateTime createdAt;
  const InspectionUpload({
    required this.clientPhotoId,
    required this.proposalId,
    required this.slot,
    required this.filePath,
    required this.takenAt,
    this.latitude,
    this.longitude,
    required this.status,
    required this.attempts,
    required this.nextAttemptAt,
    this.lastErrorCode,
    this.remotePhotoId,
    required this.createdAt,
  });
  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    map['client_photo_id'] = Variable<String>(clientPhotoId);
    map['proposal_id'] = Variable<String>(proposalId);
    map['slot'] = Variable<String>(slot);
    map['file_path'] = Variable<String>(filePath);
    map['taken_at'] = Variable<DateTime>(takenAt);
    if (!nullToAbsent || latitude != null) {
      map['latitude'] = Variable<double>(latitude);
    }
    if (!nullToAbsent || longitude != null) {
      map['longitude'] = Variable<double>(longitude);
    }
    map['status'] = Variable<String>(status);
    map['attempts'] = Variable<int>(attempts);
    map['next_attempt_at'] = Variable<DateTime>(nextAttemptAt);
    if (!nullToAbsent || lastErrorCode != null) {
      map['last_error_code'] = Variable<String>(lastErrorCode);
    }
    if (!nullToAbsent || remotePhotoId != null) {
      map['remote_photo_id'] = Variable<String>(remotePhotoId);
    }
    map['created_at'] = Variable<DateTime>(createdAt);
    return map;
  }

  InspectionUploadsCompanion toCompanion(bool nullToAbsent) {
    return InspectionUploadsCompanion(
      clientPhotoId: Value(clientPhotoId),
      proposalId: Value(proposalId),
      slot: Value(slot),
      filePath: Value(filePath),
      takenAt: Value(takenAt),
      latitude: latitude == null && nullToAbsent
          ? const Value.absent()
          : Value(latitude),
      longitude: longitude == null && nullToAbsent
          ? const Value.absent()
          : Value(longitude),
      status: Value(status),
      attempts: Value(attempts),
      nextAttemptAt: Value(nextAttemptAt),
      lastErrorCode: lastErrorCode == null && nullToAbsent
          ? const Value.absent()
          : Value(lastErrorCode),
      remotePhotoId: remotePhotoId == null && nullToAbsent
          ? const Value.absent()
          : Value(remotePhotoId),
      createdAt: Value(createdAt),
    );
  }

  factory InspectionUpload.fromJson(
    Map<String, dynamic> json, {
    ValueSerializer? serializer,
  }) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return InspectionUpload(
      clientPhotoId: serializer.fromJson<String>(json['clientPhotoId']),
      proposalId: serializer.fromJson<String>(json['proposalId']),
      slot: serializer.fromJson<String>(json['slot']),
      filePath: serializer.fromJson<String>(json['filePath']),
      takenAt: serializer.fromJson<DateTime>(json['takenAt']),
      latitude: serializer.fromJson<double?>(json['latitude']),
      longitude: serializer.fromJson<double?>(json['longitude']),
      status: serializer.fromJson<String>(json['status']),
      attempts: serializer.fromJson<int>(json['attempts']),
      nextAttemptAt: serializer.fromJson<DateTime>(json['nextAttemptAt']),
      lastErrorCode: serializer.fromJson<String?>(json['lastErrorCode']),
      remotePhotoId: serializer.fromJson<String?>(json['remotePhotoId']),
      createdAt: serializer.fromJson<DateTime>(json['createdAt']),
    );
  }
  @override
  Map<String, dynamic> toJson({ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return <String, dynamic>{
      'clientPhotoId': serializer.toJson<String>(clientPhotoId),
      'proposalId': serializer.toJson<String>(proposalId),
      'slot': serializer.toJson<String>(slot),
      'filePath': serializer.toJson<String>(filePath),
      'takenAt': serializer.toJson<DateTime>(takenAt),
      'latitude': serializer.toJson<double?>(latitude),
      'longitude': serializer.toJson<double?>(longitude),
      'status': serializer.toJson<String>(status),
      'attempts': serializer.toJson<int>(attempts),
      'nextAttemptAt': serializer.toJson<DateTime>(nextAttemptAt),
      'lastErrorCode': serializer.toJson<String?>(lastErrorCode),
      'remotePhotoId': serializer.toJson<String?>(remotePhotoId),
      'createdAt': serializer.toJson<DateTime>(createdAt),
    };
  }

  InspectionUpload copyWith({
    String? clientPhotoId,
    String? proposalId,
    String? slot,
    String? filePath,
    DateTime? takenAt,
    Value<double?> latitude = const Value.absent(),
    Value<double?> longitude = const Value.absent(),
    String? status,
    int? attempts,
    DateTime? nextAttemptAt,
    Value<String?> lastErrorCode = const Value.absent(),
    Value<String?> remotePhotoId = const Value.absent(),
    DateTime? createdAt,
  }) => InspectionUpload(
    clientPhotoId: clientPhotoId ?? this.clientPhotoId,
    proposalId: proposalId ?? this.proposalId,
    slot: slot ?? this.slot,
    filePath: filePath ?? this.filePath,
    takenAt: takenAt ?? this.takenAt,
    latitude: latitude.present ? latitude.value : this.latitude,
    longitude: longitude.present ? longitude.value : this.longitude,
    status: status ?? this.status,
    attempts: attempts ?? this.attempts,
    nextAttemptAt: nextAttemptAt ?? this.nextAttemptAt,
    lastErrorCode: lastErrorCode.present
        ? lastErrorCode.value
        : this.lastErrorCode,
    remotePhotoId: remotePhotoId.present
        ? remotePhotoId.value
        : this.remotePhotoId,
    createdAt: createdAt ?? this.createdAt,
  );
  InspectionUpload copyWithCompanion(InspectionUploadsCompanion data) {
    return InspectionUpload(
      clientPhotoId: data.clientPhotoId.present
          ? data.clientPhotoId.value
          : this.clientPhotoId,
      proposalId: data.proposalId.present
          ? data.proposalId.value
          : this.proposalId,
      slot: data.slot.present ? data.slot.value : this.slot,
      filePath: data.filePath.present ? data.filePath.value : this.filePath,
      takenAt: data.takenAt.present ? data.takenAt.value : this.takenAt,
      latitude: data.latitude.present ? data.latitude.value : this.latitude,
      longitude: data.longitude.present ? data.longitude.value : this.longitude,
      status: data.status.present ? data.status.value : this.status,
      attempts: data.attempts.present ? data.attempts.value : this.attempts,
      nextAttemptAt: data.nextAttemptAt.present
          ? data.nextAttemptAt.value
          : this.nextAttemptAt,
      lastErrorCode: data.lastErrorCode.present
          ? data.lastErrorCode.value
          : this.lastErrorCode,
      remotePhotoId: data.remotePhotoId.present
          ? data.remotePhotoId.value
          : this.remotePhotoId,
      createdAt: data.createdAt.present ? data.createdAt.value : this.createdAt,
    );
  }

  @override
  String toString() {
    return (StringBuffer('InspectionUpload(')
          ..write('clientPhotoId: $clientPhotoId, ')
          ..write('proposalId: $proposalId, ')
          ..write('slot: $slot, ')
          ..write('filePath: $filePath, ')
          ..write('takenAt: $takenAt, ')
          ..write('latitude: $latitude, ')
          ..write('longitude: $longitude, ')
          ..write('status: $status, ')
          ..write('attempts: $attempts, ')
          ..write('nextAttemptAt: $nextAttemptAt, ')
          ..write('lastErrorCode: $lastErrorCode, ')
          ..write('remotePhotoId: $remotePhotoId, ')
          ..write('createdAt: $createdAt')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode => Object.hash(
    clientPhotoId,
    proposalId,
    slot,
    filePath,
    takenAt,
    latitude,
    longitude,
    status,
    attempts,
    nextAttemptAt,
    lastErrorCode,
    remotePhotoId,
    createdAt,
  );
  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      (other is InspectionUpload &&
          other.clientPhotoId == this.clientPhotoId &&
          other.proposalId == this.proposalId &&
          other.slot == this.slot &&
          other.filePath == this.filePath &&
          other.takenAt == this.takenAt &&
          other.latitude == this.latitude &&
          other.longitude == this.longitude &&
          other.status == this.status &&
          other.attempts == this.attempts &&
          other.nextAttemptAt == this.nextAttemptAt &&
          other.lastErrorCode == this.lastErrorCode &&
          other.remotePhotoId == this.remotePhotoId &&
          other.createdAt == this.createdAt);
}

class InspectionUploadsCompanion extends UpdateCompanion<InspectionUpload> {
  final Value<String> clientPhotoId;
  final Value<String> proposalId;
  final Value<String> slot;
  final Value<String> filePath;
  final Value<DateTime> takenAt;
  final Value<double?> latitude;
  final Value<double?> longitude;
  final Value<String> status;
  final Value<int> attempts;
  final Value<DateTime> nextAttemptAt;
  final Value<String?> lastErrorCode;
  final Value<String?> remotePhotoId;
  final Value<DateTime> createdAt;
  final Value<int> rowid;
  const InspectionUploadsCompanion({
    this.clientPhotoId = const Value.absent(),
    this.proposalId = const Value.absent(),
    this.slot = const Value.absent(),
    this.filePath = const Value.absent(),
    this.takenAt = const Value.absent(),
    this.latitude = const Value.absent(),
    this.longitude = const Value.absent(),
    this.status = const Value.absent(),
    this.attempts = const Value.absent(),
    this.nextAttemptAt = const Value.absent(),
    this.lastErrorCode = const Value.absent(),
    this.remotePhotoId = const Value.absent(),
    this.createdAt = const Value.absent(),
    this.rowid = const Value.absent(),
  });
  InspectionUploadsCompanion.insert({
    required String clientPhotoId,
    required String proposalId,
    required String slot,
    required String filePath,
    required DateTime takenAt,
    this.latitude = const Value.absent(),
    this.longitude = const Value.absent(),
    required String status,
    this.attempts = const Value.absent(),
    required DateTime nextAttemptAt,
    this.lastErrorCode = const Value.absent(),
    this.remotePhotoId = const Value.absent(),
    required DateTime createdAt,
    this.rowid = const Value.absent(),
  }) : clientPhotoId = Value(clientPhotoId),
       proposalId = Value(proposalId),
       slot = Value(slot),
       filePath = Value(filePath),
       takenAt = Value(takenAt),
       status = Value(status),
       nextAttemptAt = Value(nextAttemptAt),
       createdAt = Value(createdAt);
  static Insertable<InspectionUpload> custom({
    Expression<String>? clientPhotoId,
    Expression<String>? proposalId,
    Expression<String>? slot,
    Expression<String>? filePath,
    Expression<DateTime>? takenAt,
    Expression<double>? latitude,
    Expression<double>? longitude,
    Expression<String>? status,
    Expression<int>? attempts,
    Expression<DateTime>? nextAttemptAt,
    Expression<String>? lastErrorCode,
    Expression<String>? remotePhotoId,
    Expression<DateTime>? createdAt,
    Expression<int>? rowid,
  }) {
    return RawValuesInsertable({
      if (clientPhotoId != null) 'client_photo_id': clientPhotoId,
      if (proposalId != null) 'proposal_id': proposalId,
      if (slot != null) 'slot': slot,
      if (filePath != null) 'file_path': filePath,
      if (takenAt != null) 'taken_at': takenAt,
      if (latitude != null) 'latitude': latitude,
      if (longitude != null) 'longitude': longitude,
      if (status != null) 'status': status,
      if (attempts != null) 'attempts': attempts,
      if (nextAttemptAt != null) 'next_attempt_at': nextAttemptAt,
      if (lastErrorCode != null) 'last_error_code': lastErrorCode,
      if (remotePhotoId != null) 'remote_photo_id': remotePhotoId,
      if (createdAt != null) 'created_at': createdAt,
      if (rowid != null) 'rowid': rowid,
    });
  }

  InspectionUploadsCompanion copyWith({
    Value<String>? clientPhotoId,
    Value<String>? proposalId,
    Value<String>? slot,
    Value<String>? filePath,
    Value<DateTime>? takenAt,
    Value<double?>? latitude,
    Value<double?>? longitude,
    Value<String>? status,
    Value<int>? attempts,
    Value<DateTime>? nextAttemptAt,
    Value<String?>? lastErrorCode,
    Value<String?>? remotePhotoId,
    Value<DateTime>? createdAt,
    Value<int>? rowid,
  }) {
    return InspectionUploadsCompanion(
      clientPhotoId: clientPhotoId ?? this.clientPhotoId,
      proposalId: proposalId ?? this.proposalId,
      slot: slot ?? this.slot,
      filePath: filePath ?? this.filePath,
      takenAt: takenAt ?? this.takenAt,
      latitude: latitude ?? this.latitude,
      longitude: longitude ?? this.longitude,
      status: status ?? this.status,
      attempts: attempts ?? this.attempts,
      nextAttemptAt: nextAttemptAt ?? this.nextAttemptAt,
      lastErrorCode: lastErrorCode ?? this.lastErrorCode,
      remotePhotoId: remotePhotoId ?? this.remotePhotoId,
      createdAt: createdAt ?? this.createdAt,
      rowid: rowid ?? this.rowid,
    );
  }

  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    if (clientPhotoId.present) {
      map['client_photo_id'] = Variable<String>(clientPhotoId.value);
    }
    if (proposalId.present) {
      map['proposal_id'] = Variable<String>(proposalId.value);
    }
    if (slot.present) {
      map['slot'] = Variable<String>(slot.value);
    }
    if (filePath.present) {
      map['file_path'] = Variable<String>(filePath.value);
    }
    if (takenAt.present) {
      map['taken_at'] = Variable<DateTime>(takenAt.value);
    }
    if (latitude.present) {
      map['latitude'] = Variable<double>(latitude.value);
    }
    if (longitude.present) {
      map['longitude'] = Variable<double>(longitude.value);
    }
    if (status.present) {
      map['status'] = Variable<String>(status.value);
    }
    if (attempts.present) {
      map['attempts'] = Variable<int>(attempts.value);
    }
    if (nextAttemptAt.present) {
      map['next_attempt_at'] = Variable<DateTime>(nextAttemptAt.value);
    }
    if (lastErrorCode.present) {
      map['last_error_code'] = Variable<String>(lastErrorCode.value);
    }
    if (remotePhotoId.present) {
      map['remote_photo_id'] = Variable<String>(remotePhotoId.value);
    }
    if (createdAt.present) {
      map['created_at'] = Variable<DateTime>(createdAt.value);
    }
    if (rowid.present) {
      map['rowid'] = Variable<int>(rowid.value);
    }
    return map;
  }

  @override
  String toString() {
    return (StringBuffer('InspectionUploadsCompanion(')
          ..write('clientPhotoId: $clientPhotoId, ')
          ..write('proposalId: $proposalId, ')
          ..write('slot: $slot, ')
          ..write('filePath: $filePath, ')
          ..write('takenAt: $takenAt, ')
          ..write('latitude: $latitude, ')
          ..write('longitude: $longitude, ')
          ..write('status: $status, ')
          ..write('attempts: $attempts, ')
          ..write('nextAttemptAt: $nextAttemptAt, ')
          ..write('lastErrorCode: $lastErrorCode, ')
          ..write('remotePhotoId: $remotePhotoId, ')
          ..write('createdAt: $createdAt, ')
          ..write('rowid: $rowid')
          ..write(')'))
        .toString();
  }
}

class $InspectionSubmissionsTable extends InspectionSubmissions
    with TableInfo<$InspectionSubmissionsTable, InspectionSubmission> {
  @override
  final GeneratedDatabase attachedDatabase;
  final String? _alias;
  $InspectionSubmissionsTable(this.attachedDatabase, [this._alias]);
  static const VerificationMeta _proposalIdMeta = const VerificationMeta(
    'proposalId',
  );
  @override
  late final GeneratedColumn<String> proposalId = GeneratedColumn<String>(
    'proposal_id',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _odometerKmMeta = const VerificationMeta(
    'odometerKm',
  );
  @override
  late final GeneratedColumn<int> odometerKm = GeneratedColumn<int>(
    'odometer_km',
    aliasedName,
    false,
    type: DriftSqlType.int,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _statusMeta = const VerificationMeta('status');
  @override
  late final GeneratedColumn<String> status = GeneratedColumn<String>(
    'status',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _attemptsMeta = const VerificationMeta(
    'attempts',
  );
  @override
  late final GeneratedColumn<int> attempts = GeneratedColumn<int>(
    'attempts',
    aliasedName,
    false,
    type: DriftSqlType.int,
    requiredDuringInsert: false,
    defaultValue: const Constant(0),
  );
  static const VerificationMeta _nextAttemptAtMeta = const VerificationMeta(
    'nextAttemptAt',
  );
  @override
  late final GeneratedColumn<DateTime> nextAttemptAt =
      GeneratedColumn<DateTime>(
        'next_attempt_at',
        aliasedName,
        false,
        type: DriftSqlType.dateTime,
        requiredDuringInsert: true,
      );
  static const VerificationMeta _lastErrorCodeMeta = const VerificationMeta(
    'lastErrorCode',
  );
  @override
  late final GeneratedColumn<String> lastErrorCode = GeneratedColumn<String>(
    'last_error_code',
    aliasedName,
    true,
    type: DriftSqlType.string,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _createdAtMeta = const VerificationMeta(
    'createdAt',
  );
  @override
  late final GeneratedColumn<DateTime> createdAt = GeneratedColumn<DateTime>(
    'created_at',
    aliasedName,
    false,
    type: DriftSqlType.dateTime,
    requiredDuringInsert: true,
  );
  @override
  List<GeneratedColumn> get $columns => [
    proposalId,
    odometerKm,
    status,
    attempts,
    nextAttemptAt,
    lastErrorCode,
    createdAt,
  ];
  @override
  String get aliasedName => _alias ?? actualTableName;
  @override
  String get actualTableName => $name;
  static const String $name = 'inspection_submissions';
  @override
  VerificationContext validateIntegrity(
    Insertable<InspectionSubmission> instance, {
    bool isInserting = false,
  }) {
    final context = VerificationContext();
    final data = instance.toColumns(true);
    if (data.containsKey('proposal_id')) {
      context.handle(
        _proposalIdMeta,
        proposalId.isAcceptableOrUnknown(data['proposal_id']!, _proposalIdMeta),
      );
    } else if (isInserting) {
      context.missing(_proposalIdMeta);
    }
    if (data.containsKey('odometer_km')) {
      context.handle(
        _odometerKmMeta,
        odometerKm.isAcceptableOrUnknown(data['odometer_km']!, _odometerKmMeta),
      );
    } else if (isInserting) {
      context.missing(_odometerKmMeta);
    }
    if (data.containsKey('status')) {
      context.handle(
        _statusMeta,
        status.isAcceptableOrUnknown(data['status']!, _statusMeta),
      );
    } else if (isInserting) {
      context.missing(_statusMeta);
    }
    if (data.containsKey('attempts')) {
      context.handle(
        _attemptsMeta,
        attempts.isAcceptableOrUnknown(data['attempts']!, _attemptsMeta),
      );
    }
    if (data.containsKey('next_attempt_at')) {
      context.handle(
        _nextAttemptAtMeta,
        nextAttemptAt.isAcceptableOrUnknown(
          data['next_attempt_at']!,
          _nextAttemptAtMeta,
        ),
      );
    } else if (isInserting) {
      context.missing(_nextAttemptAtMeta);
    }
    if (data.containsKey('last_error_code')) {
      context.handle(
        _lastErrorCodeMeta,
        lastErrorCode.isAcceptableOrUnknown(
          data['last_error_code']!,
          _lastErrorCodeMeta,
        ),
      );
    }
    if (data.containsKey('created_at')) {
      context.handle(
        _createdAtMeta,
        createdAt.isAcceptableOrUnknown(data['created_at']!, _createdAtMeta),
      );
    } else if (isInserting) {
      context.missing(_createdAtMeta);
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {proposalId};
  @override
  InspectionSubmission map(Map<String, dynamic> data, {String? tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : '';
    return InspectionSubmission(
      proposalId: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}proposal_id'],
      )!,
      odometerKm: attachedDatabase.typeMapping.read(
        DriftSqlType.int,
        data['${effectivePrefix}odometer_km'],
      )!,
      status: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}status'],
      )!,
      attempts: attachedDatabase.typeMapping.read(
        DriftSqlType.int,
        data['${effectivePrefix}attempts'],
      )!,
      nextAttemptAt: attachedDatabase.typeMapping.read(
        DriftSqlType.dateTime,
        data['${effectivePrefix}next_attempt_at'],
      )!,
      lastErrorCode: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}last_error_code'],
      ),
      createdAt: attachedDatabase.typeMapping.read(
        DriftSqlType.dateTime,
        data['${effectivePrefix}created_at'],
      )!,
    );
  }

  @override
  $InspectionSubmissionsTable createAlias(String alias) {
    return $InspectionSubmissionsTable(attachedDatabase, alias);
  }
}

class InspectionSubmission extends DataClass
    implements Insertable<InspectionSubmission> {
  final String proposalId;
  final int odometerKm;
  final String status;
  final int attempts;
  final DateTime nextAttemptAt;
  final String? lastErrorCode;
  final DateTime createdAt;
  const InspectionSubmission({
    required this.proposalId,
    required this.odometerKm,
    required this.status,
    required this.attempts,
    required this.nextAttemptAt,
    this.lastErrorCode,
    required this.createdAt,
  });
  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    map['proposal_id'] = Variable<String>(proposalId);
    map['odometer_km'] = Variable<int>(odometerKm);
    map['status'] = Variable<String>(status);
    map['attempts'] = Variable<int>(attempts);
    map['next_attempt_at'] = Variable<DateTime>(nextAttemptAt);
    if (!nullToAbsent || lastErrorCode != null) {
      map['last_error_code'] = Variable<String>(lastErrorCode);
    }
    map['created_at'] = Variable<DateTime>(createdAt);
    return map;
  }

  InspectionSubmissionsCompanion toCompanion(bool nullToAbsent) {
    return InspectionSubmissionsCompanion(
      proposalId: Value(proposalId),
      odometerKm: Value(odometerKm),
      status: Value(status),
      attempts: Value(attempts),
      nextAttemptAt: Value(nextAttemptAt),
      lastErrorCode: lastErrorCode == null && nullToAbsent
          ? const Value.absent()
          : Value(lastErrorCode),
      createdAt: Value(createdAt),
    );
  }

  factory InspectionSubmission.fromJson(
    Map<String, dynamic> json, {
    ValueSerializer? serializer,
  }) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return InspectionSubmission(
      proposalId: serializer.fromJson<String>(json['proposalId']),
      odometerKm: serializer.fromJson<int>(json['odometerKm']),
      status: serializer.fromJson<String>(json['status']),
      attempts: serializer.fromJson<int>(json['attempts']),
      nextAttemptAt: serializer.fromJson<DateTime>(json['nextAttemptAt']),
      lastErrorCode: serializer.fromJson<String?>(json['lastErrorCode']),
      createdAt: serializer.fromJson<DateTime>(json['createdAt']),
    );
  }
  @override
  Map<String, dynamic> toJson({ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return <String, dynamic>{
      'proposalId': serializer.toJson<String>(proposalId),
      'odometerKm': serializer.toJson<int>(odometerKm),
      'status': serializer.toJson<String>(status),
      'attempts': serializer.toJson<int>(attempts),
      'nextAttemptAt': serializer.toJson<DateTime>(nextAttemptAt),
      'lastErrorCode': serializer.toJson<String?>(lastErrorCode),
      'createdAt': serializer.toJson<DateTime>(createdAt),
    };
  }

  InspectionSubmission copyWith({
    String? proposalId,
    int? odometerKm,
    String? status,
    int? attempts,
    DateTime? nextAttemptAt,
    Value<String?> lastErrorCode = const Value.absent(),
    DateTime? createdAt,
  }) => InspectionSubmission(
    proposalId: proposalId ?? this.proposalId,
    odometerKm: odometerKm ?? this.odometerKm,
    status: status ?? this.status,
    attempts: attempts ?? this.attempts,
    nextAttemptAt: nextAttemptAt ?? this.nextAttemptAt,
    lastErrorCode: lastErrorCode.present
        ? lastErrorCode.value
        : this.lastErrorCode,
    createdAt: createdAt ?? this.createdAt,
  );
  InspectionSubmission copyWithCompanion(InspectionSubmissionsCompanion data) {
    return InspectionSubmission(
      proposalId: data.proposalId.present
          ? data.proposalId.value
          : this.proposalId,
      odometerKm: data.odometerKm.present
          ? data.odometerKm.value
          : this.odometerKm,
      status: data.status.present ? data.status.value : this.status,
      attempts: data.attempts.present ? data.attempts.value : this.attempts,
      nextAttemptAt: data.nextAttemptAt.present
          ? data.nextAttemptAt.value
          : this.nextAttemptAt,
      lastErrorCode: data.lastErrorCode.present
          ? data.lastErrorCode.value
          : this.lastErrorCode,
      createdAt: data.createdAt.present ? data.createdAt.value : this.createdAt,
    );
  }

  @override
  String toString() {
    return (StringBuffer('InspectionSubmission(')
          ..write('proposalId: $proposalId, ')
          ..write('odometerKm: $odometerKm, ')
          ..write('status: $status, ')
          ..write('attempts: $attempts, ')
          ..write('nextAttemptAt: $nextAttemptAt, ')
          ..write('lastErrorCode: $lastErrorCode, ')
          ..write('createdAt: $createdAt')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode => Object.hash(
    proposalId,
    odometerKm,
    status,
    attempts,
    nextAttemptAt,
    lastErrorCode,
    createdAt,
  );
  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      (other is InspectionSubmission &&
          other.proposalId == this.proposalId &&
          other.odometerKm == this.odometerKm &&
          other.status == this.status &&
          other.attempts == this.attempts &&
          other.nextAttemptAt == this.nextAttemptAt &&
          other.lastErrorCode == this.lastErrorCode &&
          other.createdAt == this.createdAt);
}

class InspectionSubmissionsCompanion
    extends UpdateCompanion<InspectionSubmission> {
  final Value<String> proposalId;
  final Value<int> odometerKm;
  final Value<String> status;
  final Value<int> attempts;
  final Value<DateTime> nextAttemptAt;
  final Value<String?> lastErrorCode;
  final Value<DateTime> createdAt;
  final Value<int> rowid;
  const InspectionSubmissionsCompanion({
    this.proposalId = const Value.absent(),
    this.odometerKm = const Value.absent(),
    this.status = const Value.absent(),
    this.attempts = const Value.absent(),
    this.nextAttemptAt = const Value.absent(),
    this.lastErrorCode = const Value.absent(),
    this.createdAt = const Value.absent(),
    this.rowid = const Value.absent(),
  });
  InspectionSubmissionsCompanion.insert({
    required String proposalId,
    required int odometerKm,
    required String status,
    this.attempts = const Value.absent(),
    required DateTime nextAttemptAt,
    this.lastErrorCode = const Value.absent(),
    required DateTime createdAt,
    this.rowid = const Value.absent(),
  }) : proposalId = Value(proposalId),
       odometerKm = Value(odometerKm),
       status = Value(status),
       nextAttemptAt = Value(nextAttemptAt),
       createdAt = Value(createdAt);
  static Insertable<InspectionSubmission> custom({
    Expression<String>? proposalId,
    Expression<int>? odometerKm,
    Expression<String>? status,
    Expression<int>? attempts,
    Expression<DateTime>? nextAttemptAt,
    Expression<String>? lastErrorCode,
    Expression<DateTime>? createdAt,
    Expression<int>? rowid,
  }) {
    return RawValuesInsertable({
      if (proposalId != null) 'proposal_id': proposalId,
      if (odometerKm != null) 'odometer_km': odometerKm,
      if (status != null) 'status': status,
      if (attempts != null) 'attempts': attempts,
      if (nextAttemptAt != null) 'next_attempt_at': nextAttemptAt,
      if (lastErrorCode != null) 'last_error_code': lastErrorCode,
      if (createdAt != null) 'created_at': createdAt,
      if (rowid != null) 'rowid': rowid,
    });
  }

  InspectionSubmissionsCompanion copyWith({
    Value<String>? proposalId,
    Value<int>? odometerKm,
    Value<String>? status,
    Value<int>? attempts,
    Value<DateTime>? nextAttemptAt,
    Value<String?>? lastErrorCode,
    Value<DateTime>? createdAt,
    Value<int>? rowid,
  }) {
    return InspectionSubmissionsCompanion(
      proposalId: proposalId ?? this.proposalId,
      odometerKm: odometerKm ?? this.odometerKm,
      status: status ?? this.status,
      attempts: attempts ?? this.attempts,
      nextAttemptAt: nextAttemptAt ?? this.nextAttemptAt,
      lastErrorCode: lastErrorCode ?? this.lastErrorCode,
      createdAt: createdAt ?? this.createdAt,
      rowid: rowid ?? this.rowid,
    );
  }

  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    if (proposalId.present) {
      map['proposal_id'] = Variable<String>(proposalId.value);
    }
    if (odometerKm.present) {
      map['odometer_km'] = Variable<int>(odometerKm.value);
    }
    if (status.present) {
      map['status'] = Variable<String>(status.value);
    }
    if (attempts.present) {
      map['attempts'] = Variable<int>(attempts.value);
    }
    if (nextAttemptAt.present) {
      map['next_attempt_at'] = Variable<DateTime>(nextAttemptAt.value);
    }
    if (lastErrorCode.present) {
      map['last_error_code'] = Variable<String>(lastErrorCode.value);
    }
    if (createdAt.present) {
      map['created_at'] = Variable<DateTime>(createdAt.value);
    }
    if (rowid.present) {
      map['rowid'] = Variable<int>(rowid.value);
    }
    return map;
  }

  @override
  String toString() {
    return (StringBuffer('InspectionSubmissionsCompanion(')
          ..write('proposalId: $proposalId, ')
          ..write('odometerKm: $odometerKm, ')
          ..write('status: $status, ')
          ..write('attempts: $attempts, ')
          ..write('nextAttemptAt: $nextAttemptAt, ')
          ..write('lastErrorCode: $lastErrorCode, ')
          ..write('createdAt: $createdAt, ')
          ..write('rowid: $rowid')
          ..write(')'))
        .toString();
  }
}

class $CachedPoliciesTable extends CachedPolicies
    with TableInfo<$CachedPoliciesTable, CachedPolicy> {
  @override
  final GeneratedDatabase attachedDatabase;
  final String? _alias;
  $CachedPoliciesTable(this.attachedDatabase, [this._alias]);
  static const VerificationMeta _idMeta = const VerificationMeta('id');
  @override
  late final GeneratedColumn<String> id = GeneratedColumn<String>(
    'id',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _jsonMeta = const VerificationMeta('json');
  @override
  late final GeneratedColumn<String> json = GeneratedColumn<String>(
    'json',
    aliasedName,
    true,
    type: DriftSqlType.string,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _summaryJsonMeta = const VerificationMeta(
    'summaryJson',
  );
  @override
  late final GeneratedColumn<String> summaryJson = GeneratedColumn<String>(
    'summary_json',
    aliasedName,
    true,
    type: DriftSqlType.string,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _statusMeta = const VerificationMeta('status');
  @override
  late final GeneratedColumn<String> status = GeneratedColumn<String>(
    'status',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _endAtMeta = const VerificationMeta('endAt');
  @override
  late final GeneratedColumn<DateTime> endAt = GeneratedColumn<DateTime>(
    'end_at',
    aliasedName,
    false,
    type: DriftSqlType.dateTime,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _syncedAtMeta = const VerificationMeta(
    'syncedAt',
  );
  @override
  late final GeneratedColumn<DateTime> syncedAt = GeneratedColumn<DateTime>(
    'synced_at',
    aliasedName,
    false,
    type: DriftSqlType.dateTime,
    requiredDuringInsert: true,
  );
  @override
  List<GeneratedColumn> get $columns => [
    id,
    json,
    summaryJson,
    status,
    endAt,
    syncedAt,
  ];
  @override
  String get aliasedName => _alias ?? actualTableName;
  @override
  String get actualTableName => $name;
  static const String $name = 'cached_policies';
  @override
  VerificationContext validateIntegrity(
    Insertable<CachedPolicy> instance, {
    bool isInserting = false,
  }) {
    final context = VerificationContext();
    final data = instance.toColumns(true);
    if (data.containsKey('id')) {
      context.handle(_idMeta, id.isAcceptableOrUnknown(data['id']!, _idMeta));
    } else if (isInserting) {
      context.missing(_idMeta);
    }
    if (data.containsKey('json')) {
      context.handle(
        _jsonMeta,
        json.isAcceptableOrUnknown(data['json']!, _jsonMeta),
      );
    }
    if (data.containsKey('summary_json')) {
      context.handle(
        _summaryJsonMeta,
        summaryJson.isAcceptableOrUnknown(
          data['summary_json']!,
          _summaryJsonMeta,
        ),
      );
    }
    if (data.containsKey('status')) {
      context.handle(
        _statusMeta,
        status.isAcceptableOrUnknown(data['status']!, _statusMeta),
      );
    } else if (isInserting) {
      context.missing(_statusMeta);
    }
    if (data.containsKey('end_at')) {
      context.handle(
        _endAtMeta,
        endAt.isAcceptableOrUnknown(data['end_at']!, _endAtMeta),
      );
    } else if (isInserting) {
      context.missing(_endAtMeta);
    }
    if (data.containsKey('synced_at')) {
      context.handle(
        _syncedAtMeta,
        syncedAt.isAcceptableOrUnknown(data['synced_at']!, _syncedAtMeta),
      );
    } else if (isInserting) {
      context.missing(_syncedAtMeta);
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {id};
  @override
  CachedPolicy map(Map<String, dynamic> data, {String? tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : '';
    return CachedPolicy(
      id: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}id'],
      )!,
      json: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}json'],
      ),
      summaryJson: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}summary_json'],
      ),
      status: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}status'],
      )!,
      endAt: attachedDatabase.typeMapping.read(
        DriftSqlType.dateTime,
        data['${effectivePrefix}end_at'],
      )!,
      syncedAt: attachedDatabase.typeMapping.read(
        DriftSqlType.dateTime,
        data['${effectivePrefix}synced_at'],
      )!,
    );
  }

  @override
  $CachedPoliciesTable createAlias(String alias) {
    return $CachedPoliciesTable(attachedDatabase, alias);
  }
}

class CachedPolicy extends DataClass implements Insertable<CachedPolicy> {
  final String id;
  final String? json;
  final String? summaryJson;
  final String status;
  final DateTime endAt;
  final DateTime syncedAt;
  const CachedPolicy({
    required this.id,
    this.json,
    this.summaryJson,
    required this.status,
    required this.endAt,
    required this.syncedAt,
  });
  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    map['id'] = Variable<String>(id);
    if (!nullToAbsent || json != null) {
      map['json'] = Variable<String>(json);
    }
    if (!nullToAbsent || summaryJson != null) {
      map['summary_json'] = Variable<String>(summaryJson);
    }
    map['status'] = Variable<String>(status);
    map['end_at'] = Variable<DateTime>(endAt);
    map['synced_at'] = Variable<DateTime>(syncedAt);
    return map;
  }

  CachedPoliciesCompanion toCompanion(bool nullToAbsent) {
    return CachedPoliciesCompanion(
      id: Value(id),
      json: json == null && nullToAbsent ? const Value.absent() : Value(json),
      summaryJson: summaryJson == null && nullToAbsent
          ? const Value.absent()
          : Value(summaryJson),
      status: Value(status),
      endAt: Value(endAt),
      syncedAt: Value(syncedAt),
    );
  }

  factory CachedPolicy.fromJson(
    Map<String, dynamic> json, {
    ValueSerializer? serializer,
  }) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return CachedPolicy(
      id: serializer.fromJson<String>(json['id']),
      json: serializer.fromJson<String?>(json['json']),
      summaryJson: serializer.fromJson<String?>(json['summaryJson']),
      status: serializer.fromJson<String>(json['status']),
      endAt: serializer.fromJson<DateTime>(json['endAt']),
      syncedAt: serializer.fromJson<DateTime>(json['syncedAt']),
    );
  }
  @override
  Map<String, dynamic> toJson({ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return <String, dynamic>{
      'id': serializer.toJson<String>(id),
      'json': serializer.toJson<String?>(json),
      'summaryJson': serializer.toJson<String?>(summaryJson),
      'status': serializer.toJson<String>(status),
      'endAt': serializer.toJson<DateTime>(endAt),
      'syncedAt': serializer.toJson<DateTime>(syncedAt),
    };
  }

  CachedPolicy copyWith({
    String? id,
    Value<String?> json = const Value.absent(),
    Value<String?> summaryJson = const Value.absent(),
    String? status,
    DateTime? endAt,
    DateTime? syncedAt,
  }) => CachedPolicy(
    id: id ?? this.id,
    json: json.present ? json.value : this.json,
    summaryJson: summaryJson.present ? summaryJson.value : this.summaryJson,
    status: status ?? this.status,
    endAt: endAt ?? this.endAt,
    syncedAt: syncedAt ?? this.syncedAt,
  );
  CachedPolicy copyWithCompanion(CachedPoliciesCompanion data) {
    return CachedPolicy(
      id: data.id.present ? data.id.value : this.id,
      json: data.json.present ? data.json.value : this.json,
      summaryJson: data.summaryJson.present
          ? data.summaryJson.value
          : this.summaryJson,
      status: data.status.present ? data.status.value : this.status,
      endAt: data.endAt.present ? data.endAt.value : this.endAt,
      syncedAt: data.syncedAt.present ? data.syncedAt.value : this.syncedAt,
    );
  }

  @override
  String toString() {
    return (StringBuffer('CachedPolicy(')
          ..write('id: $id, ')
          ..write('json: $json, ')
          ..write('summaryJson: $summaryJson, ')
          ..write('status: $status, ')
          ..write('endAt: $endAt, ')
          ..write('syncedAt: $syncedAt')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode =>
      Object.hash(id, json, summaryJson, status, endAt, syncedAt);
  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      (other is CachedPolicy &&
          other.id == this.id &&
          other.json == this.json &&
          other.summaryJson == this.summaryJson &&
          other.status == this.status &&
          other.endAt == this.endAt &&
          other.syncedAt == this.syncedAt);
}

class CachedPoliciesCompanion extends UpdateCompanion<CachedPolicy> {
  final Value<String> id;
  final Value<String?> json;
  final Value<String?> summaryJson;
  final Value<String> status;
  final Value<DateTime> endAt;
  final Value<DateTime> syncedAt;
  final Value<int> rowid;
  const CachedPoliciesCompanion({
    this.id = const Value.absent(),
    this.json = const Value.absent(),
    this.summaryJson = const Value.absent(),
    this.status = const Value.absent(),
    this.endAt = const Value.absent(),
    this.syncedAt = const Value.absent(),
    this.rowid = const Value.absent(),
  });
  CachedPoliciesCompanion.insert({
    required String id,
    this.json = const Value.absent(),
    this.summaryJson = const Value.absent(),
    required String status,
    required DateTime endAt,
    required DateTime syncedAt,
    this.rowid = const Value.absent(),
  }) : id = Value(id),
       status = Value(status),
       endAt = Value(endAt),
       syncedAt = Value(syncedAt);
  static Insertable<CachedPolicy> custom({
    Expression<String>? id,
    Expression<String>? json,
    Expression<String>? summaryJson,
    Expression<String>? status,
    Expression<DateTime>? endAt,
    Expression<DateTime>? syncedAt,
    Expression<int>? rowid,
  }) {
    return RawValuesInsertable({
      if (id != null) 'id': id,
      if (json != null) 'json': json,
      if (summaryJson != null) 'summary_json': summaryJson,
      if (status != null) 'status': status,
      if (endAt != null) 'end_at': endAt,
      if (syncedAt != null) 'synced_at': syncedAt,
      if (rowid != null) 'rowid': rowid,
    });
  }

  CachedPoliciesCompanion copyWith({
    Value<String>? id,
    Value<String?>? json,
    Value<String?>? summaryJson,
    Value<String>? status,
    Value<DateTime>? endAt,
    Value<DateTime>? syncedAt,
    Value<int>? rowid,
  }) {
    return CachedPoliciesCompanion(
      id: id ?? this.id,
      json: json ?? this.json,
      summaryJson: summaryJson ?? this.summaryJson,
      status: status ?? this.status,
      endAt: endAt ?? this.endAt,
      syncedAt: syncedAt ?? this.syncedAt,
      rowid: rowid ?? this.rowid,
    );
  }

  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    if (id.present) {
      map['id'] = Variable<String>(id.value);
    }
    if (json.present) {
      map['json'] = Variable<String>(json.value);
    }
    if (summaryJson.present) {
      map['summary_json'] = Variable<String>(summaryJson.value);
    }
    if (status.present) {
      map['status'] = Variable<String>(status.value);
    }
    if (endAt.present) {
      map['end_at'] = Variable<DateTime>(endAt.value);
    }
    if (syncedAt.present) {
      map['synced_at'] = Variable<DateTime>(syncedAt.value);
    }
    if (rowid.present) {
      map['rowid'] = Variable<int>(rowid.value);
    }
    return map;
  }

  @override
  String toString() {
    return (StringBuffer('CachedPoliciesCompanion(')
          ..write('id: $id, ')
          ..write('json: $json, ')
          ..write('summaryJson: $summaryJson, ')
          ..write('status: $status, ')
          ..write('endAt: $endAt, ')
          ..write('syncedAt: $syncedAt, ')
          ..write('rowid: $rowid')
          ..write(')'))
        .toString();
  }
}

class $CachedDocumentsTable extends CachedDocuments
    with TableInfo<$CachedDocumentsTable, CachedDocument> {
  @override
  final GeneratedDatabase attachedDatabase;
  final String? _alias;
  $CachedDocumentsTable(this.attachedDatabase, [this._alias]);
  static const VerificationMeta _fileIdMeta = const VerificationMeta('fileId');
  @override
  late final GeneratedColumn<String> fileId = GeneratedColumn<String>(
    'file_id',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _policyIdMeta = const VerificationMeta(
    'policyId',
  );
  @override
  late final GeneratedColumn<String> policyId = GeneratedColumn<String>(
    'policy_id',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _typeMeta = const VerificationMeta('type');
  @override
  late final GeneratedColumn<String> type = GeneratedColumn<String>(
    'type',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _fileNameMeta = const VerificationMeta(
    'fileName',
  );
  @override
  late final GeneratedColumn<String> fileName = GeneratedColumn<String>(
    'file_name',
    aliasedName,
    true,
    type: DriftSqlType.string,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _localPathMeta = const VerificationMeta(
    'localPath',
  );
  @override
  late final GeneratedColumn<String> localPath = GeneratedColumn<String>(
    'local_path',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _downloadedAtMeta = const VerificationMeta(
    'downloadedAt',
  );
  @override
  late final GeneratedColumn<DateTime> downloadedAt = GeneratedColumn<DateTime>(
    'downloaded_at',
    aliasedName,
    false,
    type: DriftSqlType.dateTime,
    requiredDuringInsert: true,
  );
  @override
  List<GeneratedColumn> get $columns => [
    fileId,
    policyId,
    type,
    fileName,
    localPath,
    downloadedAt,
  ];
  @override
  String get aliasedName => _alias ?? actualTableName;
  @override
  String get actualTableName => $name;
  static const String $name = 'cached_documents';
  @override
  VerificationContext validateIntegrity(
    Insertable<CachedDocument> instance, {
    bool isInserting = false,
  }) {
    final context = VerificationContext();
    final data = instance.toColumns(true);
    if (data.containsKey('file_id')) {
      context.handle(
        _fileIdMeta,
        fileId.isAcceptableOrUnknown(data['file_id']!, _fileIdMeta),
      );
    } else if (isInserting) {
      context.missing(_fileIdMeta);
    }
    if (data.containsKey('policy_id')) {
      context.handle(
        _policyIdMeta,
        policyId.isAcceptableOrUnknown(data['policy_id']!, _policyIdMeta),
      );
    } else if (isInserting) {
      context.missing(_policyIdMeta);
    }
    if (data.containsKey('type')) {
      context.handle(
        _typeMeta,
        type.isAcceptableOrUnknown(data['type']!, _typeMeta),
      );
    } else if (isInserting) {
      context.missing(_typeMeta);
    }
    if (data.containsKey('file_name')) {
      context.handle(
        _fileNameMeta,
        fileName.isAcceptableOrUnknown(data['file_name']!, _fileNameMeta),
      );
    }
    if (data.containsKey('local_path')) {
      context.handle(
        _localPathMeta,
        localPath.isAcceptableOrUnknown(data['local_path']!, _localPathMeta),
      );
    } else if (isInserting) {
      context.missing(_localPathMeta);
    }
    if (data.containsKey('downloaded_at')) {
      context.handle(
        _downloadedAtMeta,
        downloadedAt.isAcceptableOrUnknown(
          data['downloaded_at']!,
          _downloadedAtMeta,
        ),
      );
    } else if (isInserting) {
      context.missing(_downloadedAtMeta);
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {fileId};
  @override
  CachedDocument map(Map<String, dynamic> data, {String? tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : '';
    return CachedDocument(
      fileId: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}file_id'],
      )!,
      policyId: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}policy_id'],
      )!,
      type: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}type'],
      )!,
      fileName: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}file_name'],
      ),
      localPath: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}local_path'],
      )!,
      downloadedAt: attachedDatabase.typeMapping.read(
        DriftSqlType.dateTime,
        data['${effectivePrefix}downloaded_at'],
      )!,
    );
  }

  @override
  $CachedDocumentsTable createAlias(String alias) {
    return $CachedDocumentsTable(attachedDatabase, alias);
  }
}

class CachedDocument extends DataClass implements Insertable<CachedDocument> {
  final String fileId;
  final String policyId;
  final String type;
  final String? fileName;
  final String localPath;
  final DateTime downloadedAt;
  const CachedDocument({
    required this.fileId,
    required this.policyId,
    required this.type,
    this.fileName,
    required this.localPath,
    required this.downloadedAt,
  });
  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    map['file_id'] = Variable<String>(fileId);
    map['policy_id'] = Variable<String>(policyId);
    map['type'] = Variable<String>(type);
    if (!nullToAbsent || fileName != null) {
      map['file_name'] = Variable<String>(fileName);
    }
    map['local_path'] = Variable<String>(localPath);
    map['downloaded_at'] = Variable<DateTime>(downloadedAt);
    return map;
  }

  CachedDocumentsCompanion toCompanion(bool nullToAbsent) {
    return CachedDocumentsCompanion(
      fileId: Value(fileId),
      policyId: Value(policyId),
      type: Value(type),
      fileName: fileName == null && nullToAbsent
          ? const Value.absent()
          : Value(fileName),
      localPath: Value(localPath),
      downloadedAt: Value(downloadedAt),
    );
  }

  factory CachedDocument.fromJson(
    Map<String, dynamic> json, {
    ValueSerializer? serializer,
  }) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return CachedDocument(
      fileId: serializer.fromJson<String>(json['fileId']),
      policyId: serializer.fromJson<String>(json['policyId']),
      type: serializer.fromJson<String>(json['type']),
      fileName: serializer.fromJson<String?>(json['fileName']),
      localPath: serializer.fromJson<String>(json['localPath']),
      downloadedAt: serializer.fromJson<DateTime>(json['downloadedAt']),
    );
  }
  @override
  Map<String, dynamic> toJson({ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return <String, dynamic>{
      'fileId': serializer.toJson<String>(fileId),
      'policyId': serializer.toJson<String>(policyId),
      'type': serializer.toJson<String>(type),
      'fileName': serializer.toJson<String?>(fileName),
      'localPath': serializer.toJson<String>(localPath),
      'downloadedAt': serializer.toJson<DateTime>(downloadedAt),
    };
  }

  CachedDocument copyWith({
    String? fileId,
    String? policyId,
    String? type,
    Value<String?> fileName = const Value.absent(),
    String? localPath,
    DateTime? downloadedAt,
  }) => CachedDocument(
    fileId: fileId ?? this.fileId,
    policyId: policyId ?? this.policyId,
    type: type ?? this.type,
    fileName: fileName.present ? fileName.value : this.fileName,
    localPath: localPath ?? this.localPath,
    downloadedAt: downloadedAt ?? this.downloadedAt,
  );
  CachedDocument copyWithCompanion(CachedDocumentsCompanion data) {
    return CachedDocument(
      fileId: data.fileId.present ? data.fileId.value : this.fileId,
      policyId: data.policyId.present ? data.policyId.value : this.policyId,
      type: data.type.present ? data.type.value : this.type,
      fileName: data.fileName.present ? data.fileName.value : this.fileName,
      localPath: data.localPath.present ? data.localPath.value : this.localPath,
      downloadedAt: data.downloadedAt.present
          ? data.downloadedAt.value
          : this.downloadedAt,
    );
  }

  @override
  String toString() {
    return (StringBuffer('CachedDocument(')
          ..write('fileId: $fileId, ')
          ..write('policyId: $policyId, ')
          ..write('type: $type, ')
          ..write('fileName: $fileName, ')
          ..write('localPath: $localPath, ')
          ..write('downloadedAt: $downloadedAt')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode =>
      Object.hash(fileId, policyId, type, fileName, localPath, downloadedAt);
  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      (other is CachedDocument &&
          other.fileId == this.fileId &&
          other.policyId == this.policyId &&
          other.type == this.type &&
          other.fileName == this.fileName &&
          other.localPath == this.localPath &&
          other.downloadedAt == this.downloadedAt);
}

class CachedDocumentsCompanion extends UpdateCompanion<CachedDocument> {
  final Value<String> fileId;
  final Value<String> policyId;
  final Value<String> type;
  final Value<String?> fileName;
  final Value<String> localPath;
  final Value<DateTime> downloadedAt;
  final Value<int> rowid;
  const CachedDocumentsCompanion({
    this.fileId = const Value.absent(),
    this.policyId = const Value.absent(),
    this.type = const Value.absent(),
    this.fileName = const Value.absent(),
    this.localPath = const Value.absent(),
    this.downloadedAt = const Value.absent(),
    this.rowid = const Value.absent(),
  });
  CachedDocumentsCompanion.insert({
    required String fileId,
    required String policyId,
    required String type,
    this.fileName = const Value.absent(),
    required String localPath,
    required DateTime downloadedAt,
    this.rowid = const Value.absent(),
  }) : fileId = Value(fileId),
       policyId = Value(policyId),
       type = Value(type),
       localPath = Value(localPath),
       downloadedAt = Value(downloadedAt);
  static Insertable<CachedDocument> custom({
    Expression<String>? fileId,
    Expression<String>? policyId,
    Expression<String>? type,
    Expression<String>? fileName,
    Expression<String>? localPath,
    Expression<DateTime>? downloadedAt,
    Expression<int>? rowid,
  }) {
    return RawValuesInsertable({
      if (fileId != null) 'file_id': fileId,
      if (policyId != null) 'policy_id': policyId,
      if (type != null) 'type': type,
      if (fileName != null) 'file_name': fileName,
      if (localPath != null) 'local_path': localPath,
      if (downloadedAt != null) 'downloaded_at': downloadedAt,
      if (rowid != null) 'rowid': rowid,
    });
  }

  CachedDocumentsCompanion copyWith({
    Value<String>? fileId,
    Value<String>? policyId,
    Value<String>? type,
    Value<String?>? fileName,
    Value<String>? localPath,
    Value<DateTime>? downloadedAt,
    Value<int>? rowid,
  }) {
    return CachedDocumentsCompanion(
      fileId: fileId ?? this.fileId,
      policyId: policyId ?? this.policyId,
      type: type ?? this.type,
      fileName: fileName ?? this.fileName,
      localPath: localPath ?? this.localPath,
      downloadedAt: downloadedAt ?? this.downloadedAt,
      rowid: rowid ?? this.rowid,
    );
  }

  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    if (fileId.present) {
      map['file_id'] = Variable<String>(fileId.value);
    }
    if (policyId.present) {
      map['policy_id'] = Variable<String>(policyId.value);
    }
    if (type.present) {
      map['type'] = Variable<String>(type.value);
    }
    if (fileName.present) {
      map['file_name'] = Variable<String>(fileName.value);
    }
    if (localPath.present) {
      map['local_path'] = Variable<String>(localPath.value);
    }
    if (downloadedAt.present) {
      map['downloaded_at'] = Variable<DateTime>(downloadedAt.value);
    }
    if (rowid.present) {
      map['rowid'] = Variable<int>(rowid.value);
    }
    return map;
  }

  @override
  String toString() {
    return (StringBuffer('CachedDocumentsCompanion(')
          ..write('fileId: $fileId, ')
          ..write('policyId: $policyId, ')
          ..write('type: $type, ')
          ..write('fileName: $fileName, ')
          ..write('localPath: $localPath, ')
          ..write('downloadedAt: $downloadedAt, ')
          ..write('rowid: $rowid')
          ..write(')'))
        .toString();
  }
}

abstract class _$AppDatabase extends GeneratedDatabase {
  _$AppDatabase(QueryExecutor e) : super(e);
  $AppDatabaseManager get managers => $AppDatabaseManager(this);
  late final $InspectionUploadsTable inspectionUploads =
      $InspectionUploadsTable(this);
  late final $InspectionSubmissionsTable inspectionSubmissions =
      $InspectionSubmissionsTable(this);
  late final $CachedPoliciesTable cachedPolicies = $CachedPoliciesTable(this);
  late final $CachedDocumentsTable cachedDocuments = $CachedDocumentsTable(
    this,
  );
  @override
  Iterable<TableInfo<Table, Object?>> get allTables =>
      allSchemaEntities.whereType<TableInfo<Table, Object?>>();
  @override
  List<DatabaseSchemaEntity> get allSchemaEntities => [
    inspectionUploads,
    inspectionSubmissions,
    cachedPolicies,
    cachedDocuments,
  ];
}

typedef $$InspectionUploadsTableCreateCompanionBuilder =
    InspectionUploadsCompanion Function({
      required String clientPhotoId,
      required String proposalId,
      required String slot,
      required String filePath,
      required DateTime takenAt,
      Value<double?> latitude,
      Value<double?> longitude,
      required String status,
      Value<int> attempts,
      required DateTime nextAttemptAt,
      Value<String?> lastErrorCode,
      Value<String?> remotePhotoId,
      required DateTime createdAt,
      Value<int> rowid,
    });
typedef $$InspectionUploadsTableUpdateCompanionBuilder =
    InspectionUploadsCompanion Function({
      Value<String> clientPhotoId,
      Value<String> proposalId,
      Value<String> slot,
      Value<String> filePath,
      Value<DateTime> takenAt,
      Value<double?> latitude,
      Value<double?> longitude,
      Value<String> status,
      Value<int> attempts,
      Value<DateTime> nextAttemptAt,
      Value<String?> lastErrorCode,
      Value<String?> remotePhotoId,
      Value<DateTime> createdAt,
      Value<int> rowid,
    });

class $$InspectionUploadsTableFilterComposer
    extends Composer<_$AppDatabase, $InspectionUploadsTable> {
  $$InspectionUploadsTableFilterComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnFilters<String> get clientPhotoId => $composableBuilder(
    column: $table.clientPhotoId,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get proposalId => $composableBuilder(
    column: $table.proposalId,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get slot => $composableBuilder(
    column: $table.slot,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get filePath => $composableBuilder(
    column: $table.filePath,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<DateTime> get takenAt => $composableBuilder(
    column: $table.takenAt,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<double> get latitude => $composableBuilder(
    column: $table.latitude,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<double> get longitude => $composableBuilder(
    column: $table.longitude,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get status => $composableBuilder(
    column: $table.status,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<int> get attempts => $composableBuilder(
    column: $table.attempts,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<DateTime> get nextAttemptAt => $composableBuilder(
    column: $table.nextAttemptAt,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get lastErrorCode => $composableBuilder(
    column: $table.lastErrorCode,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get remotePhotoId => $composableBuilder(
    column: $table.remotePhotoId,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<DateTime> get createdAt => $composableBuilder(
    column: $table.createdAt,
    builder: (column) => ColumnFilters(column),
  );
}

class $$InspectionUploadsTableOrderingComposer
    extends Composer<_$AppDatabase, $InspectionUploadsTable> {
  $$InspectionUploadsTableOrderingComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnOrderings<String> get clientPhotoId => $composableBuilder(
    column: $table.clientPhotoId,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get proposalId => $composableBuilder(
    column: $table.proposalId,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get slot => $composableBuilder(
    column: $table.slot,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get filePath => $composableBuilder(
    column: $table.filePath,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<DateTime> get takenAt => $composableBuilder(
    column: $table.takenAt,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<double> get latitude => $composableBuilder(
    column: $table.latitude,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<double> get longitude => $composableBuilder(
    column: $table.longitude,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get status => $composableBuilder(
    column: $table.status,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get attempts => $composableBuilder(
    column: $table.attempts,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<DateTime> get nextAttemptAt => $composableBuilder(
    column: $table.nextAttemptAt,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get lastErrorCode => $composableBuilder(
    column: $table.lastErrorCode,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get remotePhotoId => $composableBuilder(
    column: $table.remotePhotoId,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<DateTime> get createdAt => $composableBuilder(
    column: $table.createdAt,
    builder: (column) => ColumnOrderings(column),
  );
}

class $$InspectionUploadsTableAnnotationComposer
    extends Composer<_$AppDatabase, $InspectionUploadsTable> {
  $$InspectionUploadsTableAnnotationComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  GeneratedColumn<String> get clientPhotoId => $composableBuilder(
    column: $table.clientPhotoId,
    builder: (column) => column,
  );

  GeneratedColumn<String> get proposalId => $composableBuilder(
    column: $table.proposalId,
    builder: (column) => column,
  );

  GeneratedColumn<String> get slot =>
      $composableBuilder(column: $table.slot, builder: (column) => column);

  GeneratedColumn<String> get filePath =>
      $composableBuilder(column: $table.filePath, builder: (column) => column);

  GeneratedColumn<DateTime> get takenAt =>
      $composableBuilder(column: $table.takenAt, builder: (column) => column);

  GeneratedColumn<double> get latitude =>
      $composableBuilder(column: $table.latitude, builder: (column) => column);

  GeneratedColumn<double> get longitude =>
      $composableBuilder(column: $table.longitude, builder: (column) => column);

  GeneratedColumn<String> get status =>
      $composableBuilder(column: $table.status, builder: (column) => column);

  GeneratedColumn<int> get attempts =>
      $composableBuilder(column: $table.attempts, builder: (column) => column);

  GeneratedColumn<DateTime> get nextAttemptAt => $composableBuilder(
    column: $table.nextAttemptAt,
    builder: (column) => column,
  );

  GeneratedColumn<String> get lastErrorCode => $composableBuilder(
    column: $table.lastErrorCode,
    builder: (column) => column,
  );

  GeneratedColumn<String> get remotePhotoId => $composableBuilder(
    column: $table.remotePhotoId,
    builder: (column) => column,
  );

  GeneratedColumn<DateTime> get createdAt =>
      $composableBuilder(column: $table.createdAt, builder: (column) => column);
}

class $$InspectionUploadsTableTableManager
    extends
        RootTableManager<
          _$AppDatabase,
          $InspectionUploadsTable,
          InspectionUpload,
          $$InspectionUploadsTableFilterComposer,
          $$InspectionUploadsTableOrderingComposer,
          $$InspectionUploadsTableAnnotationComposer,
          $$InspectionUploadsTableCreateCompanionBuilder,
          $$InspectionUploadsTableUpdateCompanionBuilder,
          (
            InspectionUpload,
            BaseReferences<
              _$AppDatabase,
              $InspectionUploadsTable,
              InspectionUpload
            >,
          ),
          InspectionUpload,
          PrefetchHooks Function()
        > {
  $$InspectionUploadsTableTableManager(
    _$AppDatabase db,
    $InspectionUploadsTable table,
  ) : super(
        TableManagerState(
          db: db,
          table: table,
          createFilteringComposer: () =>
              $$InspectionUploadsTableFilterComposer($db: db, $table: table),
          createOrderingComposer: () =>
              $$InspectionUploadsTableOrderingComposer($db: db, $table: table),
          createComputedFieldComposer: () =>
              $$InspectionUploadsTableAnnotationComposer(
                $db: db,
                $table: table,
              ),
          updateCompanionCallback:
              ({
                Value<String> clientPhotoId = const Value.absent(),
                Value<String> proposalId = const Value.absent(),
                Value<String> slot = const Value.absent(),
                Value<String> filePath = const Value.absent(),
                Value<DateTime> takenAt = const Value.absent(),
                Value<double?> latitude = const Value.absent(),
                Value<double?> longitude = const Value.absent(),
                Value<String> status = const Value.absent(),
                Value<int> attempts = const Value.absent(),
                Value<DateTime> nextAttemptAt = const Value.absent(),
                Value<String?> lastErrorCode = const Value.absent(),
                Value<String?> remotePhotoId = const Value.absent(),
                Value<DateTime> createdAt = const Value.absent(),
                Value<int> rowid = const Value.absent(),
              }) => InspectionUploadsCompanion(
                clientPhotoId: clientPhotoId,
                proposalId: proposalId,
                slot: slot,
                filePath: filePath,
                takenAt: takenAt,
                latitude: latitude,
                longitude: longitude,
                status: status,
                attempts: attempts,
                nextAttemptAt: nextAttemptAt,
                lastErrorCode: lastErrorCode,
                remotePhotoId: remotePhotoId,
                createdAt: createdAt,
                rowid: rowid,
              ),
          createCompanionCallback:
              ({
                required String clientPhotoId,
                required String proposalId,
                required String slot,
                required String filePath,
                required DateTime takenAt,
                Value<double?> latitude = const Value.absent(),
                Value<double?> longitude = const Value.absent(),
                required String status,
                Value<int> attempts = const Value.absent(),
                required DateTime nextAttemptAt,
                Value<String?> lastErrorCode = const Value.absent(),
                Value<String?> remotePhotoId = const Value.absent(),
                required DateTime createdAt,
                Value<int> rowid = const Value.absent(),
              }) => InspectionUploadsCompanion.insert(
                clientPhotoId: clientPhotoId,
                proposalId: proposalId,
                slot: slot,
                filePath: filePath,
                takenAt: takenAt,
                latitude: latitude,
                longitude: longitude,
                status: status,
                attempts: attempts,
                nextAttemptAt: nextAttemptAt,
                lastErrorCode: lastErrorCode,
                remotePhotoId: remotePhotoId,
                createdAt: createdAt,
                rowid: rowid,
              ),
          withReferenceMapper: (p0) => p0
              .map((e) => (e.readTable(table), BaseReferences(db, table, e)))
              .toList(),
          prefetchHooksCallback: null,
        ),
      );
}

typedef $$InspectionUploadsTableProcessedTableManager =
    ProcessedTableManager<
      _$AppDatabase,
      $InspectionUploadsTable,
      InspectionUpload,
      $$InspectionUploadsTableFilterComposer,
      $$InspectionUploadsTableOrderingComposer,
      $$InspectionUploadsTableAnnotationComposer,
      $$InspectionUploadsTableCreateCompanionBuilder,
      $$InspectionUploadsTableUpdateCompanionBuilder,
      (
        InspectionUpload,
        BaseReferences<
          _$AppDatabase,
          $InspectionUploadsTable,
          InspectionUpload
        >,
      ),
      InspectionUpload,
      PrefetchHooks Function()
    >;
typedef $$InspectionSubmissionsTableCreateCompanionBuilder =
    InspectionSubmissionsCompanion Function({
      required String proposalId,
      required int odometerKm,
      required String status,
      Value<int> attempts,
      required DateTime nextAttemptAt,
      Value<String?> lastErrorCode,
      required DateTime createdAt,
      Value<int> rowid,
    });
typedef $$InspectionSubmissionsTableUpdateCompanionBuilder =
    InspectionSubmissionsCompanion Function({
      Value<String> proposalId,
      Value<int> odometerKm,
      Value<String> status,
      Value<int> attempts,
      Value<DateTime> nextAttemptAt,
      Value<String?> lastErrorCode,
      Value<DateTime> createdAt,
      Value<int> rowid,
    });

class $$InspectionSubmissionsTableFilterComposer
    extends Composer<_$AppDatabase, $InspectionSubmissionsTable> {
  $$InspectionSubmissionsTableFilterComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnFilters<String> get proposalId => $composableBuilder(
    column: $table.proposalId,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<int> get odometerKm => $composableBuilder(
    column: $table.odometerKm,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get status => $composableBuilder(
    column: $table.status,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<int> get attempts => $composableBuilder(
    column: $table.attempts,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<DateTime> get nextAttemptAt => $composableBuilder(
    column: $table.nextAttemptAt,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get lastErrorCode => $composableBuilder(
    column: $table.lastErrorCode,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<DateTime> get createdAt => $composableBuilder(
    column: $table.createdAt,
    builder: (column) => ColumnFilters(column),
  );
}

class $$InspectionSubmissionsTableOrderingComposer
    extends Composer<_$AppDatabase, $InspectionSubmissionsTable> {
  $$InspectionSubmissionsTableOrderingComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnOrderings<String> get proposalId => $composableBuilder(
    column: $table.proposalId,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get odometerKm => $composableBuilder(
    column: $table.odometerKm,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get status => $composableBuilder(
    column: $table.status,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get attempts => $composableBuilder(
    column: $table.attempts,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<DateTime> get nextAttemptAt => $composableBuilder(
    column: $table.nextAttemptAt,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get lastErrorCode => $composableBuilder(
    column: $table.lastErrorCode,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<DateTime> get createdAt => $composableBuilder(
    column: $table.createdAt,
    builder: (column) => ColumnOrderings(column),
  );
}

class $$InspectionSubmissionsTableAnnotationComposer
    extends Composer<_$AppDatabase, $InspectionSubmissionsTable> {
  $$InspectionSubmissionsTableAnnotationComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  GeneratedColumn<String> get proposalId => $composableBuilder(
    column: $table.proposalId,
    builder: (column) => column,
  );

  GeneratedColumn<int> get odometerKm => $composableBuilder(
    column: $table.odometerKm,
    builder: (column) => column,
  );

  GeneratedColumn<String> get status =>
      $composableBuilder(column: $table.status, builder: (column) => column);

  GeneratedColumn<int> get attempts =>
      $composableBuilder(column: $table.attempts, builder: (column) => column);

  GeneratedColumn<DateTime> get nextAttemptAt => $composableBuilder(
    column: $table.nextAttemptAt,
    builder: (column) => column,
  );

  GeneratedColumn<String> get lastErrorCode => $composableBuilder(
    column: $table.lastErrorCode,
    builder: (column) => column,
  );

  GeneratedColumn<DateTime> get createdAt =>
      $composableBuilder(column: $table.createdAt, builder: (column) => column);
}

class $$InspectionSubmissionsTableTableManager
    extends
        RootTableManager<
          _$AppDatabase,
          $InspectionSubmissionsTable,
          InspectionSubmission,
          $$InspectionSubmissionsTableFilterComposer,
          $$InspectionSubmissionsTableOrderingComposer,
          $$InspectionSubmissionsTableAnnotationComposer,
          $$InspectionSubmissionsTableCreateCompanionBuilder,
          $$InspectionSubmissionsTableUpdateCompanionBuilder,
          (
            InspectionSubmission,
            BaseReferences<
              _$AppDatabase,
              $InspectionSubmissionsTable,
              InspectionSubmission
            >,
          ),
          InspectionSubmission,
          PrefetchHooks Function()
        > {
  $$InspectionSubmissionsTableTableManager(
    _$AppDatabase db,
    $InspectionSubmissionsTable table,
  ) : super(
        TableManagerState(
          db: db,
          table: table,
          createFilteringComposer: () =>
              $$InspectionSubmissionsTableFilterComposer(
                $db: db,
                $table: table,
              ),
          createOrderingComposer: () =>
              $$InspectionSubmissionsTableOrderingComposer(
                $db: db,
                $table: table,
              ),
          createComputedFieldComposer: () =>
              $$InspectionSubmissionsTableAnnotationComposer(
                $db: db,
                $table: table,
              ),
          updateCompanionCallback:
              ({
                Value<String> proposalId = const Value.absent(),
                Value<int> odometerKm = const Value.absent(),
                Value<String> status = const Value.absent(),
                Value<int> attempts = const Value.absent(),
                Value<DateTime> nextAttemptAt = const Value.absent(),
                Value<String?> lastErrorCode = const Value.absent(),
                Value<DateTime> createdAt = const Value.absent(),
                Value<int> rowid = const Value.absent(),
              }) => InspectionSubmissionsCompanion(
                proposalId: proposalId,
                odometerKm: odometerKm,
                status: status,
                attempts: attempts,
                nextAttemptAt: nextAttemptAt,
                lastErrorCode: lastErrorCode,
                createdAt: createdAt,
                rowid: rowid,
              ),
          createCompanionCallback:
              ({
                required String proposalId,
                required int odometerKm,
                required String status,
                Value<int> attempts = const Value.absent(),
                required DateTime nextAttemptAt,
                Value<String?> lastErrorCode = const Value.absent(),
                required DateTime createdAt,
                Value<int> rowid = const Value.absent(),
              }) => InspectionSubmissionsCompanion.insert(
                proposalId: proposalId,
                odometerKm: odometerKm,
                status: status,
                attempts: attempts,
                nextAttemptAt: nextAttemptAt,
                lastErrorCode: lastErrorCode,
                createdAt: createdAt,
                rowid: rowid,
              ),
          withReferenceMapper: (p0) => p0
              .map((e) => (e.readTable(table), BaseReferences(db, table, e)))
              .toList(),
          prefetchHooksCallback: null,
        ),
      );
}

typedef $$InspectionSubmissionsTableProcessedTableManager =
    ProcessedTableManager<
      _$AppDatabase,
      $InspectionSubmissionsTable,
      InspectionSubmission,
      $$InspectionSubmissionsTableFilterComposer,
      $$InspectionSubmissionsTableOrderingComposer,
      $$InspectionSubmissionsTableAnnotationComposer,
      $$InspectionSubmissionsTableCreateCompanionBuilder,
      $$InspectionSubmissionsTableUpdateCompanionBuilder,
      (
        InspectionSubmission,
        BaseReferences<
          _$AppDatabase,
          $InspectionSubmissionsTable,
          InspectionSubmission
        >,
      ),
      InspectionSubmission,
      PrefetchHooks Function()
    >;
typedef $$CachedPoliciesTableCreateCompanionBuilder =
    CachedPoliciesCompanion Function({
      required String id,
      Value<String?> json,
      Value<String?> summaryJson,
      required String status,
      required DateTime endAt,
      required DateTime syncedAt,
      Value<int> rowid,
    });
typedef $$CachedPoliciesTableUpdateCompanionBuilder =
    CachedPoliciesCompanion Function({
      Value<String> id,
      Value<String?> json,
      Value<String?> summaryJson,
      Value<String> status,
      Value<DateTime> endAt,
      Value<DateTime> syncedAt,
      Value<int> rowid,
    });

class $$CachedPoliciesTableFilterComposer
    extends Composer<_$AppDatabase, $CachedPoliciesTable> {
  $$CachedPoliciesTableFilterComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnFilters<String> get id => $composableBuilder(
    column: $table.id,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get json => $composableBuilder(
    column: $table.json,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get summaryJson => $composableBuilder(
    column: $table.summaryJson,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get status => $composableBuilder(
    column: $table.status,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<DateTime> get endAt => $composableBuilder(
    column: $table.endAt,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<DateTime> get syncedAt => $composableBuilder(
    column: $table.syncedAt,
    builder: (column) => ColumnFilters(column),
  );
}

class $$CachedPoliciesTableOrderingComposer
    extends Composer<_$AppDatabase, $CachedPoliciesTable> {
  $$CachedPoliciesTableOrderingComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnOrderings<String> get id => $composableBuilder(
    column: $table.id,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get json => $composableBuilder(
    column: $table.json,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get summaryJson => $composableBuilder(
    column: $table.summaryJson,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get status => $composableBuilder(
    column: $table.status,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<DateTime> get endAt => $composableBuilder(
    column: $table.endAt,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<DateTime> get syncedAt => $composableBuilder(
    column: $table.syncedAt,
    builder: (column) => ColumnOrderings(column),
  );
}

class $$CachedPoliciesTableAnnotationComposer
    extends Composer<_$AppDatabase, $CachedPoliciesTable> {
  $$CachedPoliciesTableAnnotationComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  GeneratedColumn<String> get id =>
      $composableBuilder(column: $table.id, builder: (column) => column);

  GeneratedColumn<String> get json =>
      $composableBuilder(column: $table.json, builder: (column) => column);

  GeneratedColumn<String> get summaryJson => $composableBuilder(
    column: $table.summaryJson,
    builder: (column) => column,
  );

  GeneratedColumn<String> get status =>
      $composableBuilder(column: $table.status, builder: (column) => column);

  GeneratedColumn<DateTime> get endAt =>
      $composableBuilder(column: $table.endAt, builder: (column) => column);

  GeneratedColumn<DateTime> get syncedAt =>
      $composableBuilder(column: $table.syncedAt, builder: (column) => column);
}

class $$CachedPoliciesTableTableManager
    extends
        RootTableManager<
          _$AppDatabase,
          $CachedPoliciesTable,
          CachedPolicy,
          $$CachedPoliciesTableFilterComposer,
          $$CachedPoliciesTableOrderingComposer,
          $$CachedPoliciesTableAnnotationComposer,
          $$CachedPoliciesTableCreateCompanionBuilder,
          $$CachedPoliciesTableUpdateCompanionBuilder,
          (
            CachedPolicy,
            BaseReferences<_$AppDatabase, $CachedPoliciesTable, CachedPolicy>,
          ),
          CachedPolicy,
          PrefetchHooks Function()
        > {
  $$CachedPoliciesTableTableManager(
    _$AppDatabase db,
    $CachedPoliciesTable table,
  ) : super(
        TableManagerState(
          db: db,
          table: table,
          createFilteringComposer: () =>
              $$CachedPoliciesTableFilterComposer($db: db, $table: table),
          createOrderingComposer: () =>
              $$CachedPoliciesTableOrderingComposer($db: db, $table: table),
          createComputedFieldComposer: () =>
              $$CachedPoliciesTableAnnotationComposer($db: db, $table: table),
          updateCompanionCallback:
              ({
                Value<String> id = const Value.absent(),
                Value<String?> json = const Value.absent(),
                Value<String?> summaryJson = const Value.absent(),
                Value<String> status = const Value.absent(),
                Value<DateTime> endAt = const Value.absent(),
                Value<DateTime> syncedAt = const Value.absent(),
                Value<int> rowid = const Value.absent(),
              }) => CachedPoliciesCompanion(
                id: id,
                json: json,
                summaryJson: summaryJson,
                status: status,
                endAt: endAt,
                syncedAt: syncedAt,
                rowid: rowid,
              ),
          createCompanionCallback:
              ({
                required String id,
                Value<String?> json = const Value.absent(),
                Value<String?> summaryJson = const Value.absent(),
                required String status,
                required DateTime endAt,
                required DateTime syncedAt,
                Value<int> rowid = const Value.absent(),
              }) => CachedPoliciesCompanion.insert(
                id: id,
                json: json,
                summaryJson: summaryJson,
                status: status,
                endAt: endAt,
                syncedAt: syncedAt,
                rowid: rowid,
              ),
          withReferenceMapper: (p0) => p0
              .map((e) => (e.readTable(table), BaseReferences(db, table, e)))
              .toList(),
          prefetchHooksCallback: null,
        ),
      );
}

typedef $$CachedPoliciesTableProcessedTableManager =
    ProcessedTableManager<
      _$AppDatabase,
      $CachedPoliciesTable,
      CachedPolicy,
      $$CachedPoliciesTableFilterComposer,
      $$CachedPoliciesTableOrderingComposer,
      $$CachedPoliciesTableAnnotationComposer,
      $$CachedPoliciesTableCreateCompanionBuilder,
      $$CachedPoliciesTableUpdateCompanionBuilder,
      (
        CachedPolicy,
        BaseReferences<_$AppDatabase, $CachedPoliciesTable, CachedPolicy>,
      ),
      CachedPolicy,
      PrefetchHooks Function()
    >;
typedef $$CachedDocumentsTableCreateCompanionBuilder =
    CachedDocumentsCompanion Function({
      required String fileId,
      required String policyId,
      required String type,
      Value<String?> fileName,
      required String localPath,
      required DateTime downloadedAt,
      Value<int> rowid,
    });
typedef $$CachedDocumentsTableUpdateCompanionBuilder =
    CachedDocumentsCompanion Function({
      Value<String> fileId,
      Value<String> policyId,
      Value<String> type,
      Value<String?> fileName,
      Value<String> localPath,
      Value<DateTime> downloadedAt,
      Value<int> rowid,
    });

class $$CachedDocumentsTableFilterComposer
    extends Composer<_$AppDatabase, $CachedDocumentsTable> {
  $$CachedDocumentsTableFilterComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnFilters<String> get fileId => $composableBuilder(
    column: $table.fileId,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get policyId => $composableBuilder(
    column: $table.policyId,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get type => $composableBuilder(
    column: $table.type,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get fileName => $composableBuilder(
    column: $table.fileName,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get localPath => $composableBuilder(
    column: $table.localPath,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<DateTime> get downloadedAt => $composableBuilder(
    column: $table.downloadedAt,
    builder: (column) => ColumnFilters(column),
  );
}

class $$CachedDocumentsTableOrderingComposer
    extends Composer<_$AppDatabase, $CachedDocumentsTable> {
  $$CachedDocumentsTableOrderingComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnOrderings<String> get fileId => $composableBuilder(
    column: $table.fileId,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get policyId => $composableBuilder(
    column: $table.policyId,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get type => $composableBuilder(
    column: $table.type,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get fileName => $composableBuilder(
    column: $table.fileName,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get localPath => $composableBuilder(
    column: $table.localPath,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<DateTime> get downloadedAt => $composableBuilder(
    column: $table.downloadedAt,
    builder: (column) => ColumnOrderings(column),
  );
}

class $$CachedDocumentsTableAnnotationComposer
    extends Composer<_$AppDatabase, $CachedDocumentsTable> {
  $$CachedDocumentsTableAnnotationComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  GeneratedColumn<String> get fileId =>
      $composableBuilder(column: $table.fileId, builder: (column) => column);

  GeneratedColumn<String> get policyId =>
      $composableBuilder(column: $table.policyId, builder: (column) => column);

  GeneratedColumn<String> get type =>
      $composableBuilder(column: $table.type, builder: (column) => column);

  GeneratedColumn<String> get fileName =>
      $composableBuilder(column: $table.fileName, builder: (column) => column);

  GeneratedColumn<String> get localPath =>
      $composableBuilder(column: $table.localPath, builder: (column) => column);

  GeneratedColumn<DateTime> get downloadedAt => $composableBuilder(
    column: $table.downloadedAt,
    builder: (column) => column,
  );
}

class $$CachedDocumentsTableTableManager
    extends
        RootTableManager<
          _$AppDatabase,
          $CachedDocumentsTable,
          CachedDocument,
          $$CachedDocumentsTableFilterComposer,
          $$CachedDocumentsTableOrderingComposer,
          $$CachedDocumentsTableAnnotationComposer,
          $$CachedDocumentsTableCreateCompanionBuilder,
          $$CachedDocumentsTableUpdateCompanionBuilder,
          (
            CachedDocument,
            BaseReferences<
              _$AppDatabase,
              $CachedDocumentsTable,
              CachedDocument
            >,
          ),
          CachedDocument,
          PrefetchHooks Function()
        > {
  $$CachedDocumentsTableTableManager(
    _$AppDatabase db,
    $CachedDocumentsTable table,
  ) : super(
        TableManagerState(
          db: db,
          table: table,
          createFilteringComposer: () =>
              $$CachedDocumentsTableFilterComposer($db: db, $table: table),
          createOrderingComposer: () =>
              $$CachedDocumentsTableOrderingComposer($db: db, $table: table),
          createComputedFieldComposer: () =>
              $$CachedDocumentsTableAnnotationComposer($db: db, $table: table),
          updateCompanionCallback:
              ({
                Value<String> fileId = const Value.absent(),
                Value<String> policyId = const Value.absent(),
                Value<String> type = const Value.absent(),
                Value<String?> fileName = const Value.absent(),
                Value<String> localPath = const Value.absent(),
                Value<DateTime> downloadedAt = const Value.absent(),
                Value<int> rowid = const Value.absent(),
              }) => CachedDocumentsCompanion(
                fileId: fileId,
                policyId: policyId,
                type: type,
                fileName: fileName,
                localPath: localPath,
                downloadedAt: downloadedAt,
                rowid: rowid,
              ),
          createCompanionCallback:
              ({
                required String fileId,
                required String policyId,
                required String type,
                Value<String?> fileName = const Value.absent(),
                required String localPath,
                required DateTime downloadedAt,
                Value<int> rowid = const Value.absent(),
              }) => CachedDocumentsCompanion.insert(
                fileId: fileId,
                policyId: policyId,
                type: type,
                fileName: fileName,
                localPath: localPath,
                downloadedAt: downloadedAt,
                rowid: rowid,
              ),
          withReferenceMapper: (p0) => p0
              .map((e) => (e.readTable(table), BaseReferences(db, table, e)))
              .toList(),
          prefetchHooksCallback: null,
        ),
      );
}

typedef $$CachedDocumentsTableProcessedTableManager =
    ProcessedTableManager<
      _$AppDatabase,
      $CachedDocumentsTable,
      CachedDocument,
      $$CachedDocumentsTableFilterComposer,
      $$CachedDocumentsTableOrderingComposer,
      $$CachedDocumentsTableAnnotationComposer,
      $$CachedDocumentsTableCreateCompanionBuilder,
      $$CachedDocumentsTableUpdateCompanionBuilder,
      (
        CachedDocument,
        BaseReferences<_$AppDatabase, $CachedDocumentsTable, CachedDocument>,
      ),
      CachedDocument,
      PrefetchHooks Function()
    >;

class $AppDatabaseManager {
  final _$AppDatabase _db;
  $AppDatabaseManager(this._db);
  $$InspectionUploadsTableTableManager get inspectionUploads =>
      $$InspectionUploadsTableTableManager(_db, _db.inspectionUploads);
  $$InspectionSubmissionsTableTableManager get inspectionSubmissions =>
      $$InspectionSubmissionsTableTableManager(_db, _db.inspectionSubmissions);
  $$CachedPoliciesTableTableManager get cachedPolicies =>
      $$CachedPoliciesTableTableManager(_db, _db.cachedPolicies);
  $$CachedDocumentsTableTableManager get cachedDocuments =>
      $$CachedDocumentsTableTableManager(_db, _db.cachedDocuments);
}
