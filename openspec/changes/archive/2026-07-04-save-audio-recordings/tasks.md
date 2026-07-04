## 1. Entity and Data Model Changes

- [x] 1.1 Add `trackNumber` field (Integer, nullable) to `Sound` entity
- [x] 1.2 Add `fileType` field (FileType enum) to `Sound` entity for storage extension
- [x] 1.3 Add `@Column` annotations for new fields
- [x] 1.4 Update `SoundRepository` if needed (likely no changes required)

## 2. File Type Extension

- [x] 2.1 Add `.mp3` extension to `FileType` enum
- [x] 2.2 Add `.wav` extension to `FileType` enum
- [x] 2.3 Add `.aac` extension to `FileType` enum
- [x] 2.4 Add `.flac` extension to `FileType` enum
- [x] 2.5 Add `.ogg` extension to `FileType` enum

## 3. DTO Updates

- [x] 3.1 Update `SoundDto` (request) to include `albumId` and `name` fields
- [x] 3.2 Create `SoundResponseDto` with fields: id, name, trackNumber, fileType, s3Url, albumId, albumName
- [ ] 3.3 Add validation annotations if needed (e.g., @Min for duration)

## 4. S3 Storage Service Enhancement

- [ ] 4.1 Add method to generate presigned URL for audio files
- [ ] 4.2 Implement URL expiration (1 hour)
- [ ] 4.3 Ensure presigned URL allows GET requests
- [x] 4.4 S3 key pattern: `{id}{extension}` (e.g., `a1b2c3d4-uuid.mp3`)

## 5. Sound Service Implementation

- [x] 5.1 Implement `save()` method with file upload and DB persistence
- [x] 5.2 Add `@Transactional` annotation for atomicity
- [x] 5.3 Implement access control check (album ownership)
- [ ] 5.4 Implement S3 upload using S3Template
- [x] 5.5 Implement SoundRepository save
- [x] 5.6 Add proper exception handling (400, 401, 403, 404)
- [ ] 5.7 Implement `getSoundById()` method with access control
- [ ] 5.8 Implement `getSoundsByPlaylist()` method with access control
- [x] 5.9 Add presigned URL generation to response DTOs

## 6. Sound Controller Implementation

- [x] 6.1 Implement POST `/sound` endpoint for audio upload
- [x] 6.2 Add `@AuthenticationPrincipal` for user authentication
- [x] 6.3 Implement file upload handling (MultipartFile)
- [x] 6.4 Add `@Valid` annotation for request validation
- [ ] 6.5 Implement GET `/sound/{id}` endpoint with access control
- [ ] 6.6 Implement GET `/playlist/{id}/sounds` endpoint with access control

## 7. Error Handling and Validation

- [ ] 7.1 Create custom exception for invalid file type
- [x] 7.2 Create custom exception for access denied
- [x] 7.3 Create custom exception for album not found
- [ ] 7.4 Add global exception handler with proper HTTP status codes
- [ ] 7.5 Add file size validation (consider maximum upload size)
- [ ] 7.6 Add content-type validation for audio files

## 8. Testing

- [ ] 8.1 Write unit tests for SoundService save() method
- [ ] 8.2 Write unit tests for access control logic
- [ ] 8.3 Write unit tests for presigned URL generation
- [ ] 8.4 Write integration tests for `/sound` POST endpoint
- [ ] 8.5 Write integration tests for access control scenarios
- [ ] 8.6 Write integration tests for invalid file formats
- [ ] 8.7 Write integration tests for authentication required
- [ ] 8.8 Write integration tests for public/private album access

## 9. Configuration and Deployment

- [ ] 9.1 Add multipart file upload configuration to application.yml
- [ ] 9.2 Configure max file size and request size
- [ ] 9.3 Add S3 bucket policy for sound/ prefix (if needed)
- [ ] 9.4 Update docker-compose.yml with S3 configuration (if needed)
- [ ] 9.5 Add environment variables for S3 bucket name

## 10. Documentation

- [ ] 10.1 Update API documentation (OpenAPI/Swagger) for `/sound` endpoints
- [ ] 10.2 Document request/response DTO structures
- [ ] 10.3 Document authentication requirements
- [ ] 10.4 Document access control rules
- [ ] 10.5 Add code comments for complex logic (access control, transaction management)
