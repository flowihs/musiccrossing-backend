## Audio File Types

**Purpose:** Support for various audio file formats through FileType enum extension.

## ADDED Requirements

### Requirement: Audio file type enum extension
The FileType enum SHALL be extended to support common audio formats.

#### Scenario: Supported audio extensions
- **WHEN** system processes audio file upload
- **THEN** system accepts files with extensions: `.mp3`, `.wav`, `.aac`, `.flac`, `.ogg`
- **AND** each extension is defined in FileType enum

#### Scenario: Extension-based storage
- **WHEN** audio file is saved to S3
- **THEN** system appends correct extension from FileType to the key
- **AND** file is stored as `{id}{extension}` (flat structure, no prefix)

#### Scenario: File type extraction from filename
- **WHEN** user uploads file via multipart form data
- **THEN** system extracts extension from `originalFilename` field
- **THEN** system converts extension (e.g., `.mp3`) to FileType enum value (e.g., `MP3`)
- **AND** system uses FileType enum for S3 storage

#### Scenario: Unsupported format rejection
- **WHEN** user uploads file with extension not in FileType
- **THEN** system rejects the upload
- **AND** system returns HTTP 400 Bad Request with error message

### Requirement: New audio file types
The system SHALL support the following audio formats:
- MP3 (.mp3) - MPEG Audio Layer III
- WAV (.wav) - Waveform Audio File Format
- AAC (.aac) - Advanced Audio Coding
- FLAC (.flac) - Free Lossless Audio Codec
- OGG (.ogg) - Ogg Vorbis

#### Scenario: Upload MP3 file
- **WHEN** user uploads `.mp3` file
- **THEN** system extracts `.mp3` extension from filename
- **THEN** system maps to FileType.MP3 enum value
- **AND** system stores in S3 with key `{uuid}.mp3`

#### Scenario: Upload WAV file
- **WHEN** user uploads `.wav` file
- **THEN** system extracts `.wav` extension from filename
- **THEN** system maps to FileType.WAV enum value
- **AND** system stores in S3 with key `{uuid}.wav`

#### Scenario: Upload AAC file
- **WHEN** user uploads `.aac` file
- **THEN** system extracts `.aac` extension from filename
- **THEN** system maps to FileType.AAC enum value
- **AND** system stores in S3 with key `{uuid}.aac`

#### Scenario: Upload FLAC file
- **WHEN** user uploads `.flac` file
- **THEN** system extracts `.flac` extension from filename
- **THEN** system maps to FileType.FLAC enum value
- **AND** system stores in S3 with key `{uuid}.flac`

#### Scenario: Upload OGG file
- **WHEN** user uploads `.ogg` file
- **THEN** system extracts `.ogg` extension from filename
- **THEN** system maps to FileType.OGG enum value
- **AND** system stores in S3 with key `{uuid}.ogg`
