import { memberInstance } from './lib/index';
import axios from 'axios';
const networktarget = process.env.REACT_APP_API;
// 1. Back stage member 조회
/**
 *
 * @param {number} id
 * @param {function} success [callback]
 * @param {function} fail [callback]
 */
function searchBackStageMembmerInfo(id, success, fail) {
  const api = memberInstance();

  api.get(`/back-stage/${id}`).then(success).catch(fail);
}

// 2. Battle 기록 조회
/**
 *
 * @param {function} success [callback]
 * @param {function} fail [callback]
 */
function searchBattleLog(success, fail) {
  const api = memberInstance();

  api.get('/battlelog').then(success).catch(fail);
}

// 3. Battle 결과 등록
/**
 *
 * @param {object} body [{result: number, user1Id: number, user2Id: number, videoId: number}]
 * @param {function} success [callback]
 * @param {function} fail [callback]
 */
function registerBattleLog(success, fail) {
  const api = memberInstance();

  api.post('/member/battlelog').then(success).catch(fail);
}

// 4. member profile image 삭제
/**
 *
 * @param {function} success [callback]
 * @param {function} fail [callback]
 */
function deleteMemberImg(success, fail) {
  const api = memberInstance();

  api.delete('/member/images/delete').then(success).catch(fail);
}

// 5. member profile image 수정
/**
 *
 * @param {formData} profileImg
 * @param {function} success [callback]
 * @param {function} fail [callback]
 */
function updateMemberImg(profileImg, success, fail) {
  let accessToken = localStorage.getItem('accessToken');
  const api = axios.create({
    baseURL: networktarget + '/api/member-management',
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
  api.put('/member/image/update', profileImg).then(success).catch(fail);
}

// 6. mypage member 조회
/**
 *
 * @param {number} id
 * @param {function} success [callback]
 * @param {function} fail [callback]
 */
async function searchMypageMemberInfo(id, success, fail) {
  const api = memberInstance();
  await api.get(`/member/${id}`).then(success).catch(fail);
}

// 7. 회원탈퇴
/**
 *
 * @param {number} id
 * @param {function} success [callback]
 * @param {function} fail [callback]
 */
function deleteMember(id, success, fail) {
  const api = memberInstance();
  api.delete(`/member/${id}`).then(success).catch(fail);
}

// 8. member 배틀 정보 조회
/**
 *
 * @param {number} id
 * @param {function} success [callback]
 * @param {function} fail [callback]
 */
function searchMemberBattleInfo(id, success, fail) {
  const api = memberInstance();

  api.get(`/member/${id}/battleInfo`).then(success).catch(fail);
}

// 9. 로그아웃
/**
 *
 * @param {number} id
 * @param {function} success [callback]
 * @param {function} fail [callback]
 */
function logoutMember(id, success, fail) {
  const api = memberInstance();

  api.get(`/member/logout/${id}`).then(success).catch(fail);
}

// 10. member 정보 수정
/**
 *
 * @param {object} body [{id: number, introduce: string, nickName: string}]
 * @param {function} success [callback]
 * @param {function} fail [callback]
 */
function updateMemberInfo(body, success, fail) {
  const api = memberInstance();

  api.put(`/member/update`, JSON.stringify(body)).then(success).catch(fail);
}
export {
  searchBackStageMembmerInfo,
  searchBattleLog,
  registerBattleLog,
  deleteMemberImg,
  updateMemberImg,
  searchMypageMemberInfo,
  deleteMember,
  searchMemberBattleInfo,
  logoutMember,
  updateMemberInfo,
};
