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

abstract class _$AppDatabase extends GeneratedDatabase {
  _$AppDatabase(QueryExecutor e) : super(e);
  $AppDatabaseManager get managers => $AppDatabaseManager(this);
  late final $InspectionUploadsTable inspectionUploads =
      $InspectionUploadsTable(this);
  late final $InspectionSubmissionsTable inspectionSubmissions =
      $InspectionSubmissionsTable(this);
  @override
  Iterable<TableInfo<Table, Object?>> get allTables =>
      allSchemaEntities.whereType<TableInfo<Table, Object?>>();
  @override
  List<DatabaseSchemaEntity> get allSchemaEntities => [
    inspectionUploads,
    inspectionSubmissions,
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

class $AppDatabaseManager {
  final _$AppDatabase _db;
  $AppDatabaseManager(this._db);
  $$InspectionUploadsTableTableManager get inspectionUploads =>
      $$InspectionUploadsTableTableManager(_db, _db.inspectionUploads);
  $$InspectionSubmissionsTableTableManager get inspectionSubmissions =>
      $$InspectionSubmissionsTableTableManager(_db, _db.inspectionSubmissions);
}
