import React, { useState } from 'react';
import './Profile.css';
//http://localhost:5173/profile/upload

const Profile = () => {
    const [selectedFile, setSelectedFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState(null); // 선택한 이미지 미리보기용
    const [uploading, setUploading] = useState(false);
    const [imageUrl, setImageUrl] = useState(null);
    const [error, setError] = useState(null);
    const [userType, setUserType] = useState('users');

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            if (!file.type.startsWith('image/')) {
                setError('이미지 파일만 업로드 가능합니다.');
                return;
            }
            setSelectedFile(file);
            setError(null);

            // 선택한 이미지 미리보기 생성
            const reader = new FileReader();
            reader.onloadend = () => {
                setPreviewUrl(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };

    const handleUpload = async () => {
        if (!selectedFile) {
            setError('파일을 선택해주세요.');
            return;
        }

        setUploading(true);
        setError(null);

        try {
            const token = localStorage.getItem('token') || sessionStorage.getItem('token');

            // 사용자 타입에 따라 URL 결정
            const baseUrl = 'http://localhost:8080';
            const profileUrl = userType === 'users'
                ? `${baseUrl}/api/users/profile`
                : `${baseUrl}/api/seller/profile`;

            const presignedUrlEndpoint = userType === 'users'
                ? `${baseUrl}/api/users/profile/presigned-url`
                : `${baseUrl}/api/seller/profile/presigned-url`;

            // 0. 프로필 정보 먼저 조회 (userNickname 필요)
            const profileResponse = await fetch(profileUrl, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                }
            });

            if (!profileResponse.ok) {
                throw new Error('프로필 정보를 가져올 수 없습니다.');
            }

            const profileData = await profileResponse.json();

            // 1. Presigned URL 요청
            const response = await fetch(
                presignedUrlEndpoint,
                {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ contentType: selectedFile.type })
                }
            );

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            const { presignedUrl, key } = data;

            // 2. Presigned URL로 이미지 업로드 (PUT 요청)
            const uploadResponse = await fetch(presignedUrl, {
                method: 'PUT',
                headers: {
                    'Content-Type': selectedFile.type
                },
                body: selectedFile
            });

            if (!uploadResponse.ok) {
                throw new Error('이미지 업로드에 실패했습니다.');
            }

            // 3. Public URL 생성
            const bucket = 'wearly-project';
            const region = 'ap-northeast-2';
            const publicUrl = `https://${bucket}.s3.${region}.amazonaws.com/${key}`;

            // 4. DB에 이미지 URL 저장 (기존 프로필 정보 포함)
            const imageUpdateUrl = userType === 'users'
                ? `${baseUrl}/api/users/profile/image`
                : `${baseUrl}/api/seller/profile/image`;

            const updateResponse = await fetch(imageUpdateUrl, {
                method: 'PATCH',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    userNickname: profileData.userNickname,
                    introduction: profileData.introduction || null,
                    phoneNumber: profileData.phoneNumber || null,
                    imageUrl: publicUrl
                })
            });

            if (!updateResponse.ok) {
                throw new Error('이미지 URL 저장에 실패했습니다.');
            }

            setImageUrl(publicUrl);
            setUploading(false);
            alert('프로필 이미지가 업로드되었습니다!');
        } catch (err) {
            console.error('업로드 실패:', err);
            setError(err.message || '업로드에 실패했습니다.');
            setUploading(false);
        }
    };

    return (
        <div className="profile-upload-container">
            <h2>프로필 이미지 업로드</h2>

            {/* 사용자 타입 선택 */}
            <div style={{ marginBottom: '20px' }}>
                <label style={{ marginRight: '10px' }}>사용자 타입:</label>
                <select
                    value={userType}
                    onChange={(e) => setUserType(e.target.value)}
                    style={{ padding: '5px 10px', fontSize: '16px' }}
                >
                    <option value="users">일반 사용자 (USER)</option>
                    <option value="seller">판매자 (SELLER)</option>
                </select>
            </div>

            <div className="upload-section">
                <input
                    type="file"
                    accept="image/*"
                    onChange={handleFileChange}
                    className="file-input"
                />

                {/* 선택한 이미지 미리보기 */}
                {previewUrl && (
                    <div style={{ marginTop: '20px', marginBottom: '20px' }}>
                        <h3>선택한 이미지 미리보기</h3>
                        <img
                            src={previewUrl}
                            alt="미리보기"
                            style={{
                                maxWidth: '300px',
                                maxHeight: '300px',
                                border: '1px solid #ddd',
                                borderRadius: '8px'
                            }}
                        />
                    </div>
                )}

                {selectedFile && (
                    <div className="file-info">
                        <p>선택된 파일: {selectedFile.name}</p>
                        <p>타입: {selectedFile.type}</p>
                        <p>크기: {(selectedFile.size / 1024).toFixed(2)} KB</p>
                    </div>
                )}

                <button
                    onClick={handleUpload}
                    disabled={!selectedFile || uploading}
                    className="upload-button"
                >
                    {uploading ? '업로드 중...' : '업로드'}
                </button>
            </div>

            {error && (
                <div className="error-message">
                    {error}
                </div>
            )}

            {imageUrl && (
                <div className="image-preview">
                    <h3>업로드된 이미지</h3>
                    <img src={imageUrl} alt="프로필" className="uploaded-image" />
                    <p className="image-url">URL: {imageUrl}</p>
                </div>
            )}
        </div>
    );
};

export default Profile;