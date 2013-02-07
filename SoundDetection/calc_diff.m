
function [ diff ] = calc_diff(data, frame, centers)


    M = [];
    frameSizeHalf = size(frame, 2)/2;
    for i = 1:size(centers, 2)
        range = (centers(2, i) - frameSizeHalf + 1):(centers(2, i) + frameSizeHalf);
        if min(range) < 1 || max(range) > size(data, 2)
            M(:, i) = -frame; %(centers(1, i), :);
        else
            size(range)
            M(:, i) = data(centers(1, i), range).';
        end
    end
    
%    diff = msefun(M, frame.');
    diff = bsxfun(@minus, M, frame.');
    diff = diff.^2;
    diff = sum(diff);

end
