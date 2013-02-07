function [ legal_moves, prob_distn ] = dynamics_model( pos, movement_size, num_guesses )

    rnd = randn(1, num_guesses);
    %prob_distn = rnd.^2 / sum(rnd.^2);
    % compute n legal moves with a probability distribution, to keep the 
    % particles moving forward.  NOTE: this may not be good, as we may just
    % want to skip one particle to 
%    legal_moves = repmat(pos, 1, num_guesses);
 %  legal_moves(2, :) = int32(legal_moves(2, :)) + int32((double(-movement_size) * rnd + double(movement_size)));
    prob_distn(1) = 1;
    legal_moves(:, 1) = pos;
    legal_moves(2, 1) = legal_moves(2, 1) + uint32(movement_size);
    
end

