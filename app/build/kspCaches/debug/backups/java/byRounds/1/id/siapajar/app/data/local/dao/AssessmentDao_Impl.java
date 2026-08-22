package id.siapajar.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import id.siapajar.app.data.local.entity.AssessmentEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AssessmentDao_Impl implements AssessmentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AssessmentEntity> __insertionAdapterOfAssessmentEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSyncStatus;

  public AssessmentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAssessmentEntity = new EntityInsertionAdapter<AssessmentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `assessments` (`id`,`studentIdsJson`,`studentNamesJson`,`instrumentType`,`photoPath`,`notes`,`tpCode`,`tpTitle`,`syncStatus`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AssessmentEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getStudentIdsJson());
        statement.bindString(3, entity.getStudentNamesJson());
        statement.bindString(4, entity.getInstrumentType());
        if (entity.getPhotoPath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPhotoPath());
        }
        statement.bindString(6, entity.getNotes());
        if (entity.getTpCode() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getTpCode());
        }
        if (entity.getTpTitle() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getTpTitle());
        }
        statement.bindString(9, entity.getSyncStatus());
        statement.bindLong(10, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfUpdateSyncStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE assessments SET syncStatus = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAssessment(final AssessmentEntity assessment,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAssessmentEntity.insert(assessment);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSyncStatus(final String id, final String status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSyncStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateSyncStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AssessmentEntity>> getAllAssessments() {
    final String _sql = "SELECT * FROM assessments ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"assessments"}, new Callable<List<AssessmentEntity>>() {
      @Override
      @NonNull
      public List<AssessmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStudentIdsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "studentIdsJson");
          final int _cursorIndexOfStudentNamesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "studentNamesJson");
          final int _cursorIndexOfInstrumentType = CursorUtil.getColumnIndexOrThrow(_cursor, "instrumentType");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfTpCode = CursorUtil.getColumnIndexOrThrow(_cursor, "tpCode");
          final int _cursorIndexOfTpTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "tpTitle");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<AssessmentEntity> _result = new ArrayList<AssessmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AssessmentEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpStudentIdsJson;
            _tmpStudentIdsJson = _cursor.getString(_cursorIndexOfStudentIdsJson);
            final String _tmpStudentNamesJson;
            _tmpStudentNamesJson = _cursor.getString(_cursorIndexOfStudentNamesJson);
            final String _tmpInstrumentType;
            _tmpInstrumentType = _cursor.getString(_cursorIndexOfInstrumentType);
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final String _tmpTpCode;
            if (_cursor.isNull(_cursorIndexOfTpCode)) {
              _tmpTpCode = null;
            } else {
              _tmpTpCode = _cursor.getString(_cursorIndexOfTpCode);
            }
            final String _tmpTpTitle;
            if (_cursor.isNull(_cursorIndexOfTpTitle)) {
              _tmpTpTitle = null;
            } else {
              _tmpTpTitle = _cursor.getString(_cursorIndexOfTpTitle);
            }
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new AssessmentEntity(_tmpId,_tmpStudentIdsJson,_tmpStudentNamesJson,_tmpInstrumentType,_tmpPhotoPath,_tmpNotes,_tmpTpCode,_tmpTpTitle,_tmpSyncStatus,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<AssessmentEntity>> getAssessmentsForStudent(final String studentId) {
    final String _sql = "SELECT * FROM assessments WHERE studentIdsJson LIKE '%' || ? || '%' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, studentId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"assessments"}, new Callable<List<AssessmentEntity>>() {
      @Override
      @NonNull
      public List<AssessmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStudentIdsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "studentIdsJson");
          final int _cursorIndexOfStudentNamesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "studentNamesJson");
          final int _cursorIndexOfInstrumentType = CursorUtil.getColumnIndexOrThrow(_cursor, "instrumentType");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfTpCode = CursorUtil.getColumnIndexOrThrow(_cursor, "tpCode");
          final int _cursorIndexOfTpTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "tpTitle");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<AssessmentEntity> _result = new ArrayList<AssessmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AssessmentEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpStudentIdsJson;
            _tmpStudentIdsJson = _cursor.getString(_cursorIndexOfStudentIdsJson);
            final String _tmpStudentNamesJson;
            _tmpStudentNamesJson = _cursor.getString(_cursorIndexOfStudentNamesJson);
            final String _tmpInstrumentType;
            _tmpInstrumentType = _cursor.getString(_cursorIndexOfInstrumentType);
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final String _tmpTpCode;
            if (_cursor.isNull(_cursorIndexOfTpCode)) {
              _tmpTpCode = null;
            } else {
              _tmpTpCode = _cursor.getString(_cursorIndexOfTpCode);
            }
            final String _tmpTpTitle;
            if (_cursor.isNull(_cursorIndexOfTpTitle)) {
              _tmpTpTitle = null;
            } else {
              _tmpTpTitle = _cursor.getString(_cursorIndexOfTpTitle);
            }
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new AssessmentEntity(_tmpId,_tmpStudentIdsJson,_tmpStudentNamesJson,_tmpInstrumentType,_tmpPhotoPath,_tmpNotes,_tmpTpCode,_tmpTpTitle,_tmpSyncStatus,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getPendingAssessments(
      final Continuation<? super List<AssessmentEntity>> $completion) {
    final String _sql = "SELECT * FROM assessments WHERE syncStatus = 'PENDING'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AssessmentEntity>>() {
      @Override
      @NonNull
      public List<AssessmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStudentIdsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "studentIdsJson");
          final int _cursorIndexOfStudentNamesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "studentNamesJson");
          final int _cursorIndexOfInstrumentType = CursorUtil.getColumnIndexOrThrow(_cursor, "instrumentType");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfTpCode = CursorUtil.getColumnIndexOrThrow(_cursor, "tpCode");
          final int _cursorIndexOfTpTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "tpTitle");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<AssessmentEntity> _result = new ArrayList<AssessmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AssessmentEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpStudentIdsJson;
            _tmpStudentIdsJson = _cursor.getString(_cursorIndexOfStudentIdsJson);
            final String _tmpStudentNamesJson;
            _tmpStudentNamesJson = _cursor.getString(_cursorIndexOfStudentNamesJson);
            final String _tmpInstrumentType;
            _tmpInstrumentType = _cursor.getString(_cursorIndexOfInstrumentType);
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final String _tmpTpCode;
            if (_cursor.isNull(_cursorIndexOfTpCode)) {
              _tmpTpCode = null;
            } else {
              _tmpTpCode = _cursor.getString(_cursorIndexOfTpCode);
            }
            final String _tmpTpTitle;
            if (_cursor.isNull(_cursorIndexOfTpTitle)) {
              _tmpTpTitle = null;
            } else {
              _tmpTpTitle = _cursor.getString(_cursorIndexOfTpTitle);
            }
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new AssessmentEntity(_tmpId,_tmpStudentIdsJson,_tmpStudentNamesJson,_tmpInstrumentType,_tmpPhotoPath,_tmpNotes,_tmpTpCode,_tmpTpTitle,_tmpSyncStatus,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
