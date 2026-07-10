## ADDED Requirements

### Requirement: Audio file upload and storage
The system SHALL accept audio file uploads via the `/sound` endpoint with multipart form data and store them in S3 storage with the key format `{id}{extension}`.

#### Scenario: Successful audio upload
- **WHEN** user sends POST request to `/sound` with multipart form data containing: SoundDto JSON body and file attachment
- **AND** user is authenticated
- **AND** album exists and belongs to the authenticated user
- **THEN** system reads file bytes from multipart request
- **AND** system saves file to S3 with key `{uuid}{extension}` (flat structure, no prefix)
- **AND** system determines file type from file extension in original filename
- **AND** system calculates trackNumber as max(trackNumber) + 1 for album sounds
- **AND** system saves metadata (id, name, trackNumber, fileType, albumId) to PostgreSQL
- **AND** system returns HTTP 201 Created with sound metadata including generated s3Url

#### Scenario: Invalid file format
- **WHEN** user uploads file with unsupported extension (not in FileType enum)
- **THEN** system returns HTTP 400 Bad Request with error message
- **AND** system does not save file to S3
- **AND** system does not save metadata to DB

#### Scenario: Non-existent album
- **WHEN** user specifies non-existent albumId in SoundDto
- **THEN** system returns HTTP 404 Not Found
- **AND** system does not save file to S3
- **AND** system does not save metadata to DB

#### Scenario: Access denied - album not owned
- **WHEN** user attempts to upload to album they do not own
- **THEN** system returns HTTP 403 Forbidden
- **AND** system does not save file to S3
- **AND** system does not save metadata to DB

### Requirement: Audio access control
The system SHALL enforce access control on audio files based on album ownership.

#### Scenario: Album owner access
- **WHEN** album belongs to the authenticated user
- **AND** user requests audio from the album
- **THEN** system grants access
- **AND** system returns HTTP 200 OK with sound metadata including presigned URL

#### Scenario: Non-owner access denied
- **WHEN** album does NOT belong to the authenticated user
- **AND** user attempts to upload or access audio from the album
- **THEN** system returns HTTP 403 Forbidden
- **AND** system does not allow the operation

### Requirement: Presigned URL generation
The system SHALL generate S3 URLs for audio files stored in S3.

#### Scenario: Successful URL generation
- **WHEN** audio file is saved to S3
- **THEN** system generates URL: `s3Endpoint/bucket/{id}{extension}`
- **AND** system returns URL in response field `s3Url`

#### Scenario: URL format
- **WHEN** URL is generated
- **THEN** URL follows format: `{endpoint}/{bucket}/{id}{extension}`
- **AND** URL uses flat structure without prefixes

### Requirement: Audio metadata retrieval
The system SHALL allow retrieval of audio file metadata with S3 URL.

#### Scenario: Get audio by ID
- **WHEN** user requests audio by ID
- **AND** user is the owner of the album containing the audio
- **THEN** system returns HTTP 200 OK with sound metadata
- **AND** response includes `id`, `name`, `trackNumber`, `fileType`, `s3Url`, `albumId`, `albumName`

#### Scenario: Get sounds by playlist
- **WHEN** user requests sounds from a playlist
- **AND** user is the owner of albums containing the sounds
- **THEN** system returns HTTP 200 OK with list of sounds
- **AND** each sound includes all metadata fields including `s3Url`

### Requirement: Audio file type support
The system SHALL support multiple audio file formats via the FileType enum.

#### Scenario: Supported audio formats
- **WHEN** user uploads audio file
- **THEN** system accepts files with extensions: `.mp3`, `.wav`, `.aac`, `.flac`, `.ogg`
- **AND** system determines file type from file extension in original filename
- **AND** system stores file with correct extension from FileType

#### Scenario: Unsupported file type
- **WHEN** user uploads file with extension not in FileType enum
- **THEN** system returns HTTP 400 Bad Request
- **AND** system rejects the upload

### Requirement: Audio metadata fields
The Sound entity SHALL include trackNumber and fileType fields. Duration field is not stored.

#### Scenario: Save with trackNumber auto-calculation
- **WHEN** user uploads audio to an album
- **THEN** system calculates trackNumber as max(trackNumber) + 1 for album sounds
- **AND** system saves trackNumber to the Sound entity in PostgreSQL
- **AND** system returns trackNumber in response

#### Scenario: File type from filename
- **WHEN** user uploads file with valid extension
- **THEN** system extracts extension from original filename
- **AND** system converts extension to FileType enum value
- **AND** system saves FileType to the Sound entity
