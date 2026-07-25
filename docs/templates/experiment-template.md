# EXP-XXX: <Experiment title>

> Status: `PLANNED | BASELINE_RECORDED | COMPLETED | INCONCLUSIVE`<br>
> Learning case: `<link>`<br>
> Hypothesis owner: `<learner>`<br>
> Updated: `YYYY-MM-DD`

## 1. Question và hypothesis

> Nếu `<change/fault>`, thì `<invariant/metric>` sẽ `<expected result>` dưới `<workload>`, vì `<mechanism>`.

## 2. Environment

- Git commit:
- JDK/application version:
- OS/CPU/RAM:
- Infrastructure versions/config:
- Dataset size/distribution:
- Workload/concurrency/duration/warm-up:

## 3. Variables và controls

- Independent variable:
- Controlled variables:
- Confounding factors:
- Metric/invariant:

## 4. Reproduction procedure

1. Setup:
2. Baseline command/action:
3. Fault/change:
4. Measurement:
5. Cleanup/recovery:

## 5. Expected observations

| Signal | Baseline expectation | Changed/fault expectation |
| --- | --- | --- |
| | | |

## 6. Raw results

Giữ output hoặc link artifact chưa diễn giải; không cherry-pick số đẹp.

| Run | Input/workload | Raw value/result | Artifact link |
| --- | --- | --- | --- |
| | | | |

## 7. Summary

| Metric/invariant | Before | After | Delta/status |
| --- | --- | --- | --- |
| | | | |

## 8. Interpretation

- Hypothesis được hỗ trợ/bác bỏ/chưa kết luận:
- Causal mechanism phù hợp/không phù hợp:
- Confounding factors:
- Điều chưa đo được:
- Decision/case bị ảnh hưởng:

## 9. Reproducibility gate

- [ ] Environment và dataset đủ để tái lập.
- [ ] Baseline được chạy trước change/fault.
- [ ] Chỉ một biến chính thay đổi hoặc interaction được ghi rõ.
- [ ] Raw result được giữ lại.
- [ ] Command/procedure chạy lại đã được kiểm tra.
- [ ] Failure recovery/cleanup được xác minh khi liên quan.

## 10. Links

- Theory/deep-dive:
- Learning case:
- Code/tests:
- Raw artifacts:
- ADR/runbook:
