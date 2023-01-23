import styled from "styled-components";
import { toDateFormatOfKR } from "../../util/toDateFormatOfKR";

const Container = styled.article`
  width: 100%;
  height: 100%;
  background-color: #ffffff;
  padding: 1.5rem;
  border-radius: 0.5rem;
  margin-bottom: 1rem;
  :last-child {
    margin: 0;
  }
  > .titleWrap {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    border-bottom: solid 1px #d9d9d9;
    > .title {
      font-weight: var(--bold-weight);
      font-size: var(--big-font);
    }
    > .date {
      color: var(--sub-font-color);
      padding: .5rem 0 1rem;
    }
  }
  > .contentWrap {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    > .title {
      margin: 1rem 0 0.5rem;
      font-weight: var(--bold-weight);
    }
    > .content {
      word-break: keep-all;
      color: var(--sub-font-color);
      /* overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 4;
      -webkit-box-orient: vertical; */
    }
  }
`;
const Review = ({data}) => {
    console.log(data)
    return (
        <Container>
        <div className="titleWrap">
          <h3 className="title">{data.club.clubName}</h3>
          <span className="date">{data.club.meetingDate}</span>
        </div>
        <div className="contentWrap">
          <p className="title">{data.commentCategory}</p>
          <p className="content">
          {data.commentBody}
          </p>
        </div>
      </Container>
    )
}

export default Review